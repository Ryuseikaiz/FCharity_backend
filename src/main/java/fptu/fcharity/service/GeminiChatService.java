package fptu.fcharity.service;

import fptu.fcharity.dto.chat.ChatMessage;
import fptu.fcharity.dto.gemini.*;
import fptu.fcharity.entity.*;
import fptu.fcharity.response.project.ProjectFinalResponse;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.service.manage.project.ProjectService;
import fptu.fcharity.service.manage.request.RequestService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.ToProjectDonationRepository;
import fptu.fcharity.repository.manage.project.SpendingPlanRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.utils.constants.OrganizationStatus;
import fptu.fcharity.utils.constants.project.DonationStatus;
import fptu.fcharity.utils.constants.project.ProjectStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiChatService {

    private final RequestService requestService;
    private final ProjectService projectService;
    private final WebClient.Builder webClientBuilder;
    @Autowired private final ProjectRepository projectRepository;
    @Autowired private final ToProjectDonationRepository donationRepository;
    @Autowired private final SpendingPlanRepository spendingPlanRepository;
    @Autowired private final OrganizationRepository organizationRepository;
    @Autowired private final OrganizationMemberRepository organizationMemberRepository;

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private WebClient webClient;

    private final List<GeminiSafetySetting> safetySettings = List.of(
            new GeminiSafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_MEDIUM_AND_ABOVE"),
            new GeminiSafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"),
            new GeminiSafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_MEDIUM_AND_ABOVE"),
            new GeminiSafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_MEDIUM_AND_ABOVE")
    );

    private final GeminiGenerationConfig generationConfig = new GeminiGenerationConfig(
            0.7f, 1, 1.0f, 2048
    );

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("FATAL: Gemini API Key ('gemini.api.key') is not configured.");
            throw new IllegalStateException("Gemini API Key is missing.");
        }
        if (geminiApiUrl == null || geminiApiUrl.trim().isEmpty()) {
            log.error("FATAL: Gemini API URL ('gemini.api.url') is not configured.");
            throw new IllegalStateException("Gemini API URL is missing.");
        }
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        log.info("GeminiChatService initialized to use REST API at: {}", geminiApiUrl);
    }

    private String fetchChatContextData(String userId) {
        log.debug("Fetching context data (User ID: {})", userId != null ? userId : "N/A");
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("\n\n--- FCharity Application Context ---");
        try {
            List<RequestFinalResponse> activeRequests = requestService.getActiveRequests();
            if (!activeRequests.isEmpty()) {
                contextBuilder.append("\n--- Currently Active Requests (Sample) ---\n");
                activeRequests.stream().limit(3).forEach(req -> {
                    if (req.getHelpRequest() != null) {
                        contextBuilder.append(String.format("- ID: %s, Title: %s, Category: %s\n",
                                req.getHelpRequest().getId(),
                                req.getHelpRequest().getTitle() != null ? req.getHelpRequest().getTitle() : "N/A",
                                req.getHelpRequest().getCategory() != null ? req.getHelpRequest().getCategory().getCategoryName() : "N/A"
                        ));
                    }
                });
                contextBuilder.append(String.format("(Showing sample of %d active requests)\n", activeRequests.size()));
            } else { contextBuilder.append("\n(No active requests found)\n"); }
            contextBuilder.append("--------------------------------------\n");

            List<ProjectFinalResponse> activeProjects = projectService.getAllProjects();
            if (!activeProjects.isEmpty()) {
                contextBuilder.append("\n--- Active Projects (Sample) ---\n");
                activeProjects.stream().limit(3).forEach(proj -> {
                    if (proj.getProject() != null) {
                        contextBuilder.append(String.format("- ID: %s, Name: %s, Status: %s\n",
                                proj.getProject().getId(),
                                proj.getProject().getProjectName() != null ? proj.getProject().getProjectName() : "N/A",
                                proj.getProject().getProjectStatus() != null ? proj.getProject().getProjectStatus() : "N/A"
                        ));
                    }
                });
                contextBuilder.append(String.format("(Showing sample of %d projects)\n", activeProjects.size()));
            } else { contextBuilder.append("\n(No active projects found)\n"); }
            contextBuilder.append("-----------------------------\n");

            List<Organization> approvedOrgs = organizationRepository.findOrganizationByOrganizationStatus(OrganizationStatus.APPROVED);
            if (!approvedOrgs.isEmpty()) {
                contextBuilder.append("\n--- Approved Organizations (Sample) ---\n");
                approvedOrgs.stream().limit(3).forEach(org -> {
                    contextBuilder.append(String.format("- ID: %s, Name: %s\n",
                            org.getOrganizationId(),
                            org.getOrganizationName()
                    ));
                });
                contextBuilder.append(String.format("(Showing sample of %d approved organizations)\n", approvedOrgs.size()));
            } else {
                contextBuilder.append("\n(No approved organizations found)\n");
            }
            contextBuilder.append("------------------------------\n");

        } catch (Exception e) {
            log.error("Error fetching context data", e);
            contextBuilder.append("\n(Error retrieving context data)\n");
        }
        return contextBuilder.toString();
    }

    private String processLocalDatabaseQueries(String userMessage) {
        String normalizedMessage = userMessage.toLowerCase().trim();

        Pattern nearingCompletionPattern = Pattern.compile("(?:những )?(?:dự án|project) nào (?:donate )?(?:gần được|sắp) hoàn thành(?: mục tiêu)?|gần đủ tiền|gần xong");
        if (nearingCompletionPattern.matcher(normalizedMessage).find()) return getNearlyCompletedProjects();

        Pattern recommendationPattern = Pattern.compile("(?:nên|muốn) (?:donate|quyên góp|ủng hộ) (?:cho )?(?:dự án|project) nào(?: bây giờ)?|gợi ý (?:dự án|project)");
        if (recommendationPattern.matcher(normalizedMessage).find()) return recommendProjects();

        Pattern projectDonationPattern = Pattern.compile("(?:dá»± án|project)\\s+['\"]?(.+?)['\"]?\\s+(?:quyên góp được bao nhiêu|donate được bao nhiêu|tổng tiền|total donation|raised|how much|donation total)");
        Matcher donationMatcher = projectDonationPattern.matcher(normalizedMessage);
        if (donationMatcher.find()) return getProjectDonationTotalVietnamese(donationMatcher.group(1).trim());

        Pattern needyProjectPattern = Pattern.compile("(?:dá»± án|project)\\s+(?:nào cần quyên góp nhất|needs donation most|cần tiền nhất|top needed|most needed)");
        if (needyProjectPattern.matcher(normalizedMessage).find()) return recommendProjects();

        Pattern listOrgsPattern = Pattern.compile("^(?:liệt kê|danh sách|cho xem|show|list) (?:các )?(?:tổ chức|organization|tổ chức từ thiện)(?: đang hoạt động)?(?: nào)?\\??$");
        if (listOrgsPattern.matcher(normalizedMessage).find()) {
            log.debug("Intent detected: List organizations (Vietnamese)");
            return listOrganizations();
        }

        Pattern orgDetailsPattern = Pattern.compile("^(?:thông tin|chi tiết|details of|tell me about) (?:tổ chức|organization) ['\"]?(.+?)['\"]?\\??$");
        Matcher orgDetailsMatcher = orgDetailsPattern.matcher(normalizedMessage);
        if (orgDetailsMatcher.find()) {
            String orgQuery = orgDetailsMatcher.group(1).trim();
            log.debug("Intent detected: Get details for organization query '{}'", orgQuery);
            return getOrganizationDetails(orgQuery);
        }

        Pattern orgCeoPattern = Pattern.compile("^(?:ceo|giám đốc điều hành|người đứng đầu) (?:của )?(?:tổ chức|organization) ['\"]?(.+?)['\"]? (?:là ai|là gì)\\??$");
        Matcher orgCeoMatcher = orgCeoPattern.matcher(normalizedMessage);
        if (orgCeoMatcher.find()) {
            String orgQuery = orgCeoMatcher.group(1).trim();
            log.debug("Intent detected: Get CEO for organization query '{}'", orgQuery);
            return getOrganizationCEO(orgQuery);
        }

        Pattern orgMembersCountPattern = Pattern.compile("^(?:tổ chức|organization) ['\"]?(.+?)['\"]? có bao nhiêu thành viên\\??$");
        Matcher orgMembersCountMatcher = orgMembersCountPattern.matcher(normalizedMessage);
        if (orgMembersCountMatcher.find()) {
            String orgQuery = orgMembersCountMatcher.group(1).trim();
            log.debug("Intent detected: Get member count for organization query '{}'", orgQuery);
            return getOrganizationMemberCount(orgQuery);
        }

        return null;
    }

    private String formatLink(String type, String id, String title) {
        String displayTitle;
        String path;
        switch (type.toLowerCase()) {
            case "project":
                displayTitle = title != null && !title.trim().isEmpty() ? title : "Dự án không tên";
                path = id != null ? "/projects/" + id : null;
                break;
            case "request":
                displayTitle = title != null && !title.trim().isEmpty() ? title : "Yêu cầu không tên";
                path = id != null ? "/requests/" + id : null;
                break;
            case "organization":
                displayTitle = title != null && !title.trim().isEmpty() ? title : "Tổ chức không tên";
                path = id != null ? "/organizations/" + id : null;
                break;
            default:
                displayTitle = title != null && !title.trim().isEmpty() ? title : "Mục không tên";
                path = null;
        }
        if (path == null) return String.format("**%s** (Thiếu ID hoặc loại không hỗ trợ link)", displayTitle);
        return String.format("[**%s**](%s)", displayTitle, path);
    }

    private String getNearlyCompletedProjects() {
        final double TARGET_PERCENTAGE_THRESHOLD = 90.0;
        final int MAX_RESULTS = 5;
        List<Project> donatingProjects = projectRepository.findAllWithInclude().stream()
                .filter(p -> ProjectStatus.DONATING.equalsIgnoreCase(p.getProjectStatus()))
                .toList();
        if (donatingProjects.isEmpty()) return "Hiện tại không có dự án nào đang trong giai đoạn quyên góp gần hoàn thành.";
        List<ProjectProgress> progressList = donatingProjects.stream()
                .map(this::calculateProjectProgress)
                .filter(pp -> pp != null && pp.percentage().compareTo(BigDecimal.valueOf(TARGET_PERCENTAGE_THRESHOLD)) >= 0)
                .sorted(Comparator.comparing(ProjectProgress::percentage).reversed())
                .limit(MAX_RESULTS)
                .toList();
        if (progressList.isEmpty()) return String.format("Hiện chưa có dự án nào quyên góp đạt trên %.0f%% mục tiêu.", TARGET_PERCENTAGE_THRESHOLD);
        StringBuilder response = new StringBuilder("Dưới đây là một số dự án đang tiến gần đến mục tiêu quyên góp (đã đạt trên ");
        response.append(String.format("%.0f%%", TARGET_PERCENTAGE_THRESHOLD)).append("):\n\n");
        for (int i = 0; i < progressList.size(); i++) {
            ProjectProgress pp = progressList.get(i);
            String link = formatLink("project", pp.project().getId().toString(), pp.project().getProjectName());
            String currentFormatted = pp.current().setScale(0, RoundingMode.HALF_UP).toPlainString();
            String goalFormatted = pp.goal().setScale(0, RoundingMode.HALF_UP).toPlainString();
            response.append(String.format("%d. %s - Đã đạt: **%s%%** (%s / %s VND)\n", i + 1, link, pp.percentage().setScale(1, RoundingMode.HALF_UP), currentFormatted, goalFormatted));
        }
        if (donatingProjects.size() > progressList.size() && progressList.size() >= MAX_RESULTS) response.append("\n... và một số dự án khác cũng đang gần hoàn thành.");
        response.append("\nBạn có thể nhấp vào tên dự án để xem chi tiết và quyên góp giúp dự án hoàn thành sớm!");
        return response.toString();
    }

    private String recommendProjects() {
        final int MAX_RECOMMENDATIONS = 3;
        List<Project> donatingProjects = projectRepository.findAllWithInclude().stream()
                .filter(p -> ProjectStatus.DONATING.equalsIgnoreCase(p.getProjectStatus()))
                .toList();
        if (donatingProjects.isEmpty()) return "Hiện tại không có dự án nào đang mở quyên góp để FCharity gợi ý cho bạn.";
        List<ProjectProgress> progressList = donatingProjects.stream()
                .map(this::calculateProjectProgress)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ProjectProgress::percentage))
                .limit(MAX_RECOMMENDATIONS)
                .toList();
        if (progressList.isEmpty()) return "Không tìm thấy dự án phù hợp để gợi ý vào lúc này.";
        StringBuilder response = new StringBuilder("Việc chọn dự án nào để quyên góp thực sự phụ thuộc vào mối quan tâm của bạn. ");
        response.append("Tuy nhiên, FCharity xin gợi ý một số dự án đang cần sự chung tay nhiều nhất lúc này (dựa trên tiến độ quyên góp):\n\n");
        for (int i = 0; i < progressList.size(); i++) {
            ProjectProgress pp = progressList.get(i);
            String link = formatLink("project", pp.project().getId().toString(), pp.project().getProjectName());
            String categoryName = pp.project().getCategory() != null ? pp.project().getCategory().getCategoryName() : "Chưa phân loại";
            response.append(String.format("*   %s - Hiện đạt **%s%%** mục tiêu (Danh mục: %s)\n", link, pp.percentage().setScale(1, RoundingMode.HALF_UP), categoryName));
        }
        response.append("\nĐây chỉ là gợi ý. Bạn hãy nhấp vào tên dự án để tìm hiểu kỹ hơn và chọn dự án phù hợp nhất với mình nhé!");
        return response.toString();
    }

    private ProjectProgress calculateProjectProgress(Project project) {
        if (project == null) return null;
        SpendingPlan plan = spendingPlanRepository.findByProjectId(project.getId());
        BigDecimal goal = (plan != null && plan.getEstimatedTotalCost() != null && plan.getEstimatedTotalCost().compareTo(BigDecimal.ZERO) > 0) ? plan.getEstimatedTotalCost() : BigDecimal.ZERO;
        List<ToProjectDonation> donations = donationRepository.findByProjectId(project.getId());
        BigDecimal current = donations.stream().filter(d -> DonationStatus.COMPLETED.equalsIgnoreCase(d.getDonationStatus())).map(ToProjectDonation::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal percentage = BigDecimal.ZERO;
        if (goal.compareTo(BigDecimal.ZERO) > 0) percentage = current.divide(goal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        else if (current.compareTo(BigDecimal.ZERO) > 0) percentage = BigDecimal.valueOf(100);
        if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) percentage = BigDecimal.valueOf(100);
        if (percentage.compareTo(BigDecimal.ZERO) < 0) percentage = BigDecimal.ZERO;
        return new ProjectProgress(project, current, goal, percentage);
    }

    private record ProjectProgress(Project project, BigDecimal current, BigDecimal goal, BigDecimal percentage) {}

    private String getProjectDonationTotalVietnamese(String projectNameQuery) {
        List<Project> projects = projectRepository.findAllWithInclude();
        Optional<Project> foundProject = projects.stream().filter(p -> p.getProjectName() != null && p.getProjectName().toLowerCase().contains(projectNameQuery.toLowerCase())).findFirst();
        if (foundProject.isPresent()) {
            Project project = foundProject.get();
            List<ToProjectDonation> donations = donationRepository.findByProjectId(project.getId());
            BigDecimal totalDonated = donations.stream().filter(d -> DonationStatus.COMPLETED.equalsIgnoreCase(d.getDonationStatus())).map(ToProjectDonation::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            String formattedAmount = totalDonated.setScale(0, RoundingMode.HALF_UP).toPlainString();
            String link = formatLink("project", project.getId().toString(), project.getProjectName());
            return String.format("Dự án %s đã quyên góp được tổng cộng %s VND từ các khoản đóng góp đã hoàn thành.", link, formattedAmount);
        } else { return String.format("Xin lỗi, tôi không tìm thấy thông tin quyên góp cụ thể cho dự án có tên giống '%s' trong cơ sở dữ liệu.", projectNameQuery); }
    }

    private String listOrganizations() {
        List<Organization> approvedOrgs = organizationRepository.findOrganizationByOrganizationStatus(OrganizationStatus.APPROVED);
        if (approvedOrgs.isEmpty()) return "Hiện tại không có tổ chức nào đang hoạt động trên hệ thống.";
        StringBuilder response = new StringBuilder("Các tổ chức đang hoạt động trên FCharity:\n\n");
        approvedOrgs.forEach(org -> {
            String link = formatLink("organization", org.getOrganizationId().toString(), org.getOrganizationName());
            response.append(String.format("- %s\n", link));
        });
        return response.toString();
    }

    private Optional<Organization> findOrganizationByQuery(String query) {
        try { UUID orgId = UUID.fromString(query); return organizationRepository.findById(orgId); }
        catch (IllegalArgumentException e) { /* Continue to search by name */ }
        List<Organization> allOrgs = organizationRepository.findAll();
        return allOrgs.stream().filter(org -> org.getOrganizationName() != null && org.getOrganizationName().toLowerCase().contains(query.toLowerCase())).findFirst();
    }

    private String getOrganizationDetails(String orgQuery) {
        Optional<Organization> foundOrgOpt = findOrganizationByQuery(orgQuery);
        if (foundOrgOpt.isEmpty()) return String.format("Xin lỗi, tôi không tìm thấy thông tin cho tổ chức '%s'.", orgQuery);
        Organization org = foundOrgOpt.get();
        int memberCount = organizationMemberRepository.findByOrganizationOrganizationId(org.getOrganizationId()).size();
        String ceoName = org.getCeo() != null ? org.getCeo().getFullName() : "Chưa rõ";
        String link = formatLink("organization", org.getOrganizationId().toString(), org.getOrganizationName());
        StringBuilder response = new StringBuilder(String.format("Thông tin về tổ chức %s:\n", link));
        response.append(String.format("- **Tên đầy đủ:** %s\n", org.getOrganizationName()));
        response.append(String.format("- **Trạng thái:** %s\n", org.getOrganizationStatus()));
        response.append(String.format("- **CEO:** %s\n", ceoName));
        response.append(String.format("- **Số lượng thành viên (ước tính):** %d\n", memberCount));
        if (org.getOrganizationDescription() != null && !org.getOrganizationDescription().trim().isEmpty()) response.append(String.format("- **Mô tả:** %s\n", org.getOrganizationDescription()));
        if (org.getEmail() != null) response.append(String.format("- **Email:** %s\n", org.getEmail()));
        if (org.getPhoneNumber() != null) response.append(String.format("- **Điện thoại:** %s\n", org.getPhoneNumber()));
        return response.toString();
    }

    private String getOrganizationCEO(String orgQuery) {
        Optional<Organization> foundOrgOpt = findOrganizationByQuery(orgQuery);
        if (foundOrgOpt.isEmpty()) return String.format("Xin lỗi, tôi không tìm thấy thông tin cho tổ chức '%s'.", orgQuery);
        Organization org = foundOrgOpt.get();
        String ceoName = org.getCeo() != null ? org.getCeo().getFullName() : "chưa được chỉ định";
        String orgLink = formatLink("organization", org.getOrganizationId().toString(), org.getOrganizationName());
        return String.format("CEO của tổ chức %s là %s.", orgLink, ceoName);
    }

    private String getOrganizationMemberCount(String orgQuery) {
        Optional<Organization> foundOrgOpt = findOrganizationByQuery(orgQuery);
        if (foundOrgOpt.isEmpty()) return String.format("Xin lỗi, tôi không tìm thấy thông tin cho tổ chức '%s'.", orgQuery);
        Organization org = foundOrgOpt.get();
        int memberCount = organizationMemberRepository.findByOrganizationOrganizationId(org.getOrganizationId()).size();
        String orgLink = formatLink("organization", org.getOrganizationId().toString(), org.getOrganizationName());
        return String.format("Tổ chức %s hiện có khoảng %d thành viên.", orgLink, memberCount);
    }

    public String generateReply(String userMessage, List<ChatMessage> history, String userId) throws IOException {
        log.info("Processing message for user: {}", userId != null ? userId : "N/A");
        String localDbAnswer = processLocalDatabaseQueries(userMessage);
        if (localDbAnswer != null) {
            log.info("Query handled locally using database data for user: {}.", userId != null ? userId : "N/A");
            return localDbAnswer;
        }
        log.info("Query not handled locally, calling Gemini API for user: {}", userId != null ? userId : "N/A");
        String context = fetchChatContextData(userId);
        String systemInstruction = """
            You are FCharity Assistant, a helpful AI integrated into the FCharity platform.
            - Your primary role is to assist users with information about the FCharity platform.
            - Be friendly, professional, and concise. **Respond in the same language as the user's last query (Vietnamese or English).**
            - **Priority 1: Answer questions about FCharity based *only* on the provided Application Context.**
                - Use the context (active requests, projects, organizations) provided below to answer specific questions.
                - If the context doesn't contain the information, state that you don't have specific details *in the provided context* and suggest searching the platform or asking for more general help. **Do not invent FCharity details.**
                - Provide links using Markdown: [**Item Title**](/requests/{id}), [**Item Title**](/projects/{id}), or [**Item Title**](/organizations/{id}). Use the ID from the context if available.
                - For **admin contact** information, provide: Phone: 0828006916, Facebook: https://www.facebook.com/dtrg.1101/
            - **Priority 2: If the question is clearly outside the scope of FCharity or the provided context (e.g., general knowledge, math problems, coding questions), answer it generally as a helpful AI assistant.** Do not preface with "I cannot answer based on the context". Just answer the general question directly.
            - **Refuse** to answer questions that are harmful, unethical, hateful, sexually explicit, or promote dangerous activities, citing safety reasons.
            """;
        List<GeminiRequestContent> contents = new ArrayList<>();
        contents.add(new GeminiRequestContent("user", List.of(new GeminiRequestPart(systemInstruction + context))));
        contents.add(new GeminiRequestContent("model", List.of(new GeminiRequestPart("Okay, I understand my role and the FCharity context. How can I assist you?"))));
        if (history != null) {
            history.forEach(msg -> {
                String apiRole = msg.getRole().equalsIgnoreCase("model") ? "model" : "user";
                if (msg.getText() != null && !msg.getText().trim().isEmpty()) {
                    contents.add(new GeminiRequestContent(apiRole, List.of(new GeminiRequestPart(msg.getText()))));
                } else { log.warn("Skipping empty or invalid history message: {}", msg); }
            });
        }
        contents.add(new GeminiRequestContent("user", List.of(new GeminiRequestPart(userMessage))));
        GeminiApiRequest apiRequest = GeminiApiRequest.builder().contents(contents).safetySettings(safetySettings).generationConfig(generationConfig).build();
        try {
            log.debug("Sending request to Gemini API for user: {}", userId != null ? userId : "N/A");
            GeminiApiResponse apiResponse = webClient.post()
                    .uri(geminiApiUrl + "?key=" + apiKey)
                    .bodyValue(apiRequest)
                    .retrieve()
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS), cr -> cr.bodyToMono(String.class).defaultIfEmpty("[No Body]").flatMap(b -> Mono.<IOException>error(new IOException("Rate limit: " + b))))
                    .onStatus(status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST), cr -> cr.bodyToMono(String.class).defaultIfEmpty("[No Body]").flatMap(b -> Mono.<IOException>error(new IOException("Bad request (400): " + b))))
                    .onStatus(status -> status.is4xxClientError() && !status.isSameCodeAs(HttpStatus.BAD_REQUEST) && !status.isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS), this::handleClientError)
                    .onStatus(status -> status.is5xxServerError(), this::handleServerError)
                    .bodyToMono(GeminiApiResponse.class)
                    .block();
            log.debug("Received response from Gemini API for user: {}", userId != null ? userId : "N/A");
            if (apiResponse == null) throw new IOException("Received null response.");
            if (apiResponse.getPromptFeedback() != null && apiResponse.getPromptFeedback().getBlockReason() != null) throw new IOException("Blocked by policy: " + apiResponse.getPromptFeedback().getBlockReason());
            if (apiResponse.getCandidates() == null || apiResponse.getCandidates().isEmpty()) throw new IOException("No response candidates generated.");
            GeminiCandidate candidate = apiResponse.getCandidates().get(0);
            String finishReason = candidate.getFinishReason();
            boolean blockedBySafety = candidate.getSafetyRatings() != null && candidate.getSafetyRatings().stream().anyMatch(r -> Boolean.TRUE.equals(r.getBlocked()));
            if (blockedBySafety || "SAFETY".equalsIgnoreCase(finishReason)) throw new IOException("Blocked by safety filter.");
            if (!"STOP".equalsIgnoreCase(finishReason) && !"MAX_TOKENS".equalsIgnoreCase(finishReason)) throw new IOException("Generation failed: " + finishReason);
            String replyText = "";
            if (candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) replyText = candidate.getContent().getParts().get(0).getText();
            else log.warn("Received candidate with no content parts.");
            if ("MAX_TOKENS".equalsIgnoreCase(finishReason)) replyText += "\n\n*(Response truncated)*";
            log.info("Successfully generated reply via Gemini API for user: {}", userId != null ? userId : "N/A");
            return replyText.trim();
        } catch (WebClientResponseException e) { log.error("WebClient error...", e); throw new IOException("Error communicating (" + e.getStatusCode() + ").", e); }
        catch (IOException e) { log.error("IO error...", e); throw e; }
        catch (Exception e) { log.error("Unexpected error...", e); throw new IOException("Unexpected error.", e); }
    }

    private Mono<IOException> handleClientError(ClientResponse cr) {
        HttpStatus st = (HttpStatus) cr.statusCode();
        return cr.bodyToMono(String.class).defaultIfEmpty("[No Body]").flatMap(b -> {
            log.error("Client Error {} from API: {}", st, b);
            return Mono.<IOException>error(new IOException("Client error (" + st + "). Check request."));
        }).onErrorResume(e -> {
            log.error("Client Error {} from API (Body read failed: {})", st, e.getMessage(), e);
            return Mono.<IOException>error(new IOException("Client error (Read failed)."));
        });
    }

    private Mono<IOException> handleServerError(ClientResponse cr) {
        HttpStatus st = (HttpStatus) cr.statusCode();
        return cr.bodyToMono(String.class).defaultIfEmpty("[No Body]").flatMap(b -> {
            log.error("Server Error {} from API: {}", st, b);
            return Mono.<IOException>error(new IOException("AI service error (" + st + "). Try later."));
        }).onErrorResume(e -> {
            log.error("Server Error {} from API (Body read failed: {})", st, e.getMessage(), e);
            return Mono.<IOException>error(new IOException("AI service error (Read failed)."));
        });
    }
}