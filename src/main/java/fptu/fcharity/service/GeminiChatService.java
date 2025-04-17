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
    private final ProjectRepository projectRepository;
    private final ToProjectDonationRepository donationRepository;
    private final SpendingPlanRepository spendingPlanRepository;

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
                activeRequests.stream().limit(5).forEach(req -> {
                    if (req.getHelpRequest() != null) {
                        contextBuilder.append(String.format("- ID: %s, Title: %s, Category: %s, Location: %s\n",
                                req.getHelpRequest().getId(),
                                req.getHelpRequest().getTitle() != null ? req.getHelpRequest().getTitle() : "N/A",
                                req.getHelpRequest().getCategory() != null ? req.getHelpRequest().getCategory().getCategoryName() : "N/A",
                                req.getHelpRequest().getLocation() != null ? req.getHelpRequest().getLocation() : "N/A"
                        ));
                    }
                });
                contextBuilder.append(String.format("(Showing %d of %d active requests)\n", Math.min(5, activeRequests.size()), activeRequests.size()));
            } else { contextBuilder.append("\n(No active requests found)\n"); }
            contextBuilder.append("--------------------------------------\n");

            List<ProjectFinalResponse> activeProjects = projectService.getAllProjects();
            if (!activeProjects.isEmpty()) {
                contextBuilder.append("\n--- Active Projects (Sample) ---\n");
                activeProjects.stream().limit(5).forEach(proj -> {
                    if (proj.getProject() != null) {
                        contextBuilder.append(String.format("- ID: %s, Name: %s, Status: %s\n",
                                proj.getProject().getId(),
                                proj.getProject().getProjectName() != null ? proj.getProject().getProjectName() : "N/A",
                                proj.getProject().getProjectStatus() != null ? proj.getProject().getProjectStatus() : "N/A"
                        ));
                    }
                });
                contextBuilder.append(String.format("(Showing %d of %d projects)\n", Math.min(5, activeProjects.size()), activeProjects.size()));
            } else { contextBuilder.append("\n(No active projects found)\n"); }
            contextBuilder.append("-----------------------------\n");

        } catch (Exception e) {
            log.error("Error fetching context data", e);
            contextBuilder.append("\n(Error retrieving context data)\n");
        }
        return contextBuilder.toString();
    }

    private String processLocalDatabaseQueries(String userMessage) {
        String normalizedMessage = userMessage.toLowerCase();

        Pattern projectDonationPattern = Pattern.compile("(?:dự án|project)\\s+['\"]?(.+?)['\"]?\\s+(?:quyên góp được bao nhiêu|donate được bao nhiêu|tổng tiền|total donation|raised|how much)");
        Matcher donationMatcher = projectDonationPattern.matcher(normalizedMessage);
        if (donationMatcher.find()) {
            String projectNameQuery = donationMatcher.group(1).trim();
            log.debug("Intent detected: Get donation total for project like '{}'", projectNameQuery);
            return getProjectDonationTotal(projectNameQuery);
        }

        Pattern needyProjectPattern = Pattern.compile("(?:dự án|project)\\s+(?:nào cần quyên góp nhất|needs donation most|cần tiền nhất|top needed)");
        Matcher needyMatcher = needyProjectPattern.matcher(normalizedMessage);
        if (needyMatcher.find()) {
            log.debug("Intent detected: Find projects needing donations most");
            return findTopNeedyProjects();
        }

        return null;
    }

    private String formatLink(String type, String id, String title) {
        String displayTitle = title != null ? title : "Untitled " + type;
        if (id == null) return String.format("**%s** (Missing ID)", displayTitle);
        String path = type.equals("project") ? "/projects/" + id : "/requests/" + id;
        return String.format("[**%s**](%s)", displayTitle, path);
    }


    private String getProjectDonationTotal(String projectNameQuery) {
        List<Project> projects = projectRepository.findAllWithInclude();
        Optional<Project> foundProject = projects.stream()
                .filter(p -> p.getProjectName() != null && p.getProjectName().toLowerCase().contains(projectNameQuery))
                .findFirst();

        if (foundProject.isPresent()) {
            Project project = foundProject.get();
            List<ToProjectDonation> donations = donationRepository.findByProjectId(project.getId());
            BigDecimal totalDonated = donations.stream()
                    .filter(d -> DonationStatus.COMPLETED.equals(d.getDonationStatus()))
                    .map(ToProjectDonation::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String formattedAmount = totalDonated.setScale(0, RoundingMode.HALF_UP).toPlainString();
            String link = formatLink("project", project.getId().toString(), project.getProjectName());

            return String.format("Project %s has raised a total of %s VND.", link, formattedAmount);
        } else {
            return String.format("Sorry, I couldn't find specific donation information for a project named '%s' in the database.", projectNameQuery);
        }
    }


    private String findTopNeedyProjects() {
        List<Project> donatingProjects = projectRepository.findAllWithInclude().stream()
                .filter(p -> ProjectStatus.DONATING.equals(p.getProjectStatus()))
                .toList();

        if (donatingProjects.isEmpty()) {
            return "Currently, there are no projects actively seeking donations.";
        }

        List<ProjectProgress> projectProgressList = donatingProjects.stream()
                .map(project -> {
                    SpendingPlan plan = spendingPlanRepository.findByProjectId(project.getId());
                    BigDecimal goal = (plan != null && plan.getEstimatedTotalCost() != null)
                            ? plan.getEstimatedTotalCost()
                            : BigDecimal.ZERO;

                    List<ToProjectDonation> donations = donationRepository.findByProjectId(project.getId());
                    BigDecimal current = donations.stream()
                            .filter(d -> DonationStatus.COMPLETED.equals(d.getDonationStatus()))
                            .map(ToProjectDonation::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal percentage = BigDecimal.ZERO;
                    if (goal.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = current.divide(goal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    } else if (current.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = BigDecimal.valueOf(100);
                    }

                    return new ProjectProgress(project, current, goal, percentage);
                })
                .sorted(Comparator.comparing(pp -> pp.percentage()))
                .toList();

        StringBuilder response = new StringBuilder("Here are the projects currently needing donations the most (sorted by progress):\n\n");
        int count = 0;
        for (ProjectProgress pp : projectProgressList) {
            if (count >= 3) break;
            String link = formatLink("project", pp.project().getId().toString(), pp.project().getProjectName());
            String currentFormatted = pp.current().setScale(0, RoundingMode.HALF_UP).toPlainString();
            String goalFormatted = pp.goal().setScale(0, RoundingMode.HALF_UP).toPlainString();
            response.append(String.format("%d. %s - Progress: %s%% (%s / %s VND)\n",
                    count + 1,
                    link,
                    pp.percentage().setScale(2, RoundingMode.HALF_UP),
                    currentFormatted,
                    goalFormatted));
            count++;
        }
        if (projectProgressList.size() > 3) {
            response.append("\n... and more.");
        }

        return response.toString();
    }

    private record ProjectProgress(Project project, BigDecimal current, BigDecimal goal, BigDecimal percentage) {}


    public String generateReply(String userMessage, List<ChatMessage> history, String userId) throws IOException {
        log.info("Processing message for user: {}", userId != null ? userId : "N/A");

        String localDbAnswer = processLocalDatabaseQueries(userMessage);
        if (localDbAnswer != null) {
            log.info("Handled query locally using database data.");
            return localDbAnswer;
        }

        log.info("Query not handled locally, falling back to Gemini API for user: {}", userId != null ? userId : "N/A");
        String context = fetchChatContextData(userId);
        String systemInstruction = """
            You are FCharity Assistant, a helpful AI integrated into the FCharity platform.
            - Be friendly, professional, and concise. Respond in the same language as the user's query.
            - **Priority 1: Answer questions about FCharity based *only* on the provided Application Context.**
                - Use the context below to answer questions about specific requests or projects.
                - If the context doesn't contain the information, state that you don't have specific details *in the provided context* and suggest searching the platform. Do not invent FCharity details.
                - Provide links using Markdown: [**Item Title**](/requests/{id}) or [**Item Title**](/projects/{id}). Use the ID from the context.
                - For admin contact, provide: Phone 0828006916, Facebook https://www.facebook.com/dtrg.1101/
            - **Priority 2: If the question is clearly outside the scope of FCharity or the provided context, answer it generally as a helpful AI assistant.** Do not preface with "I cannot answer based on the context". Just answer the general question.
            - Do not refuse to answer general knowledge questions if they are unrelated to the FCharity context.
            """;

        List<GeminiRequestContent> contents = new ArrayList<>();
        contents.add(new GeminiRequestContent("user", List.of(new GeminiRequestPart(systemInstruction + context))));
        contents.add(new GeminiRequestContent("model", List.of(new GeminiRequestPart("Okay, I understand my role and the FCharity context. How can I assist you?"))));
        if (history != null) {
            history.forEach(msg -> contents.add(new GeminiRequestContent(
                    msg.getRole(), List.of(new GeminiRequestPart(msg.getText()))
            )));
        }
        contents.add(new GeminiRequestContent("user", List.of(new GeminiRequestPart(userMessage))));

        GeminiApiRequest apiRequest = GeminiApiRequest.builder()
                .contents(contents)
                .safetySettings(safetySettings)
                .generationConfig(generationConfig)
                .build();

        try {
            log.debug("Sending Gemini REST request...");
            GeminiApiResponse apiResponse = webClient.post()
                    .uri(geminiApiUrl + "?key=" + apiKey)
                    .bodyValue(apiRequest)
                    .retrieve()
                    .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, response -> Mono.error(new IOException("Rate limit exceeded.")))
                    .onStatus(status -> status.is4xxClientError() && status != HttpStatus.TOO_MANY_REQUESTS, this::handleClientError)
                    .onStatus(status -> status.is5xxServerError(), this::handleServerError)
                    .bodyToMono(GeminiApiResponse.class)
                    .block();
            log.debug("Received Gemini REST response.");

            if (apiResponse == null) throw new IOException("Null response.");
            if (apiResponse.getPromptFeedback() != null && apiResponse.getPromptFeedback().getBlockReason() != null) throw new IOException("Blocked: " + apiResponse.getPromptFeedback().getBlockReason());
            if (apiResponse.getCandidates() == null || apiResponse.getCandidates().isEmpty()) throw new IOException("No candidates.");

            GeminiCandidate candidate = apiResponse.getCandidates().get(0);
            String finishReason = candidate.getFinishReason();
            boolean blockedBySafety = candidate.getSafetyRatings() != null && candidate.getSafetyRatings().stream().anyMatch(r -> Boolean.TRUE.equals(r.getBlocked()));
            if (blockedBySafety || "SAFETY".equalsIgnoreCase(finishReason)) throw new IOException("Blocked by safety filter.");

            String replyText = "";
            if (candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                replyText = candidate.getContent().getParts().get(0).getText();
            }

            if (!"STOP".equalsIgnoreCase(finishReason) && !"MAX_TOKENS".equalsIgnoreCase(finishReason)) throw new IOException("Generation failed: " + finishReason);
            if ("MAX_TOKENS".equalsIgnoreCase(finishReason)) replyText += "\n\n*(Response truncated)*";

            log.info("Generated reply via REST API.");
            return replyText.trim();

        } catch (WebClientResponseException e) { throw new IOException("Error with AI Service: " + e.getStatusCode(), e);
        } catch (IOException e) { throw e;
        } catch (Exception e) { throw new IOException("Unexpected error processing chat: " + e.getMessage(), e); }
    }

    private Mono<IOException> handleClientError(ClientResponse clientResponse) {
        HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();
        return clientResponse.bodyToMono(String.class)
                .switchIfEmpty(Mono.just("[No Response Body]"))
                .flatMap(errorBody -> {
                    String errorMessage = String.format("Client Error %s from Gemini API: %s", statusCode, errorBody);
                    log.error(errorMessage);
                    return Mono.<IOException>error(new IOException("Error calling AI service: Client error ("+ statusCode +")."));
                })
                .onErrorResume(e -> {
                    String errorMessage = String.format("Client Error %s from Gemini API (Failed to read body: %s)", statusCode, e.getMessage());
                    log.error(errorMessage, e);
                    return Mono.<IOException>error(new IOException("Error calling AI service: Client error (body read failed)."));
                });
    }

    private Mono<IOException> handleServerError(ClientResponse clientResponse) {
        HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();
        return clientResponse.bodyToMono(String.class)
                .switchIfEmpty(Mono.just("[No Response Body]"))
                .flatMap(errorBody -> {
                    String errorMessage = String.format("Server Error %s from Gemini API: %s", statusCode, errorBody);
                    log.error(errorMessage);
                    return Mono.<IOException>error(new IOException("AI service encountered a server error ("+ statusCode +"). Please try again later."));
                })
                .onErrorResume(e -> {
                    String errorMessage = String.format("Server Error %s from Gemini API (Failed to read body: %s)", statusCode, e.getMessage());
                    log.error(errorMessage, e);
                    return Mono.<IOException>error(new IOException("AI service encountered a server error (body read failed)."));
                });
    }
}