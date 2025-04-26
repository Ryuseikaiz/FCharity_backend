package fptu.fcharity.service.manage.organization.event;

import fptu.fcharity.dto.organization.IncludesExcludeEventMailAccessDTO;
import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.*;
import fptu.fcharity.helpers.email.EmailService;
import fptu.fcharity.repository.manage.organization.EventEmailAccessRepository;
import fptu.fcharity.repository.manage.organization.OrganizationEventRepository;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.OrganizationEventMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationEventServiceImpl implements OrganizationEventService {
    private final OrganizationEventRepository organizationEventRepository;
    private final OrganizationEventMapper organizationEventMapper;
    private final OrganizationRepository organizationRepository;
    private final EmailService emailService;
    private final EventEmailAccessRepository eventEmailAccessRepository;
    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    public OrganizationEventServiceImpl(
            OrganizationEventRepository organizationEventRepository,
            OrganizationEventMapper organizationEventMapper,
            OrganizationRepository organizationRepository,
            EmailService emailService,
            EventEmailAccessRepository eventEmailAccessRepository,
            UserRepository userRepository, OrganizationMemberRepository organizationMemberRepository) {
        this.organizationEventRepository = organizationEventRepository;
        this.organizationEventMapper = organizationEventMapper;
        this.organizationRepository = organizationRepository;
        this.emailService = emailService;
        this.eventEmailAccessRepository = eventEmailAccessRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationEventDTO> findByOrganizationId(UUID organizationId) {
        return organizationEventRepository
                .findOrganizationEventByOrganizerOrganizationId(organizationId)
                .stream()
                .map(organizationEventMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public OrganizationEventDTO save(OrganizationEventDTO organizationEventDTO, UUID organizationId) {
        System.out.println("🤖🤖🤖 Creating organization event: " + organizationEventDTO);
        Organization organizer = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new ApiRequestException("Organization not found"));
        OrganizationEvent event = organizationEventMapper.toEntity(organizationEventDTO);
        event.setOrganizer(organizer);

        OrganizationEvent saved = organizationEventRepository.save(event);

        return organizationEventMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public OrganizationEventDTO update(OrganizationEventDTO updatedOrganizationEventDTO) {
        OrganizationEvent event = organizationEventRepository
                .findById(updatedOrganizationEventDTO.getOrganizationEventId())
                .orElseThrow(()-> new ApiRequestException("Event not found"));

        Organization organizer = organizationRepository
                .findById(updatedOrganizationEventDTO.getOrganizer().getOrganizationId())
                .orElseThrow(() -> new ApiRequestException("Organization not found"));


        OrganizationEvent updatedEvent = organizationEventMapper.toEntity(updatedOrganizationEventDTO);

        OrganizationEvent savedEvent = organizationEventRepository.save(updatedEvent);

        return organizationEventMapper.toDTO(savedEvent);
    }

    @Override
    public OrganizationEventDTO findByOrganizationEventId(UUID organizationEventId) {
        return organizationEventMapper.toDTO(organizationEventRepository.findOrganizationEventsByOrganizationEventId((organizationEventId)));
    }

    @Override
    public boolean existsByOrganizationEventId(UUID organizationEventId) {
        if (organizationEventRepository.existsOrganizationEventByOrganizationEventId(organizationEventId)) return true;
        return false;
    }

    @Override
    @Transactional
    public void deleteByOrganizationEventId(UUID organizationEventId) {
        organizationEventRepository.deleteById((organizationEventId));
    }

    @Override
    public IncludesExcludeEventMailAccessDTO getIncludesExcludes(UUID organizationEventId) {
        List<EventEmailAccess> includes = eventEmailAccessRepository.findEventEmailAccessByOrganizationEventOrganizationEventIdAndAccessType(organizationEventId, EventEmailAccess.AccessType.INCLUDE);
        List<EventEmailAccess> excludes = eventEmailAccessRepository.findEventEmailAccessByOrganizationEventOrganizationEventIdAndAccessType(organizationEventId, EventEmailAccess.AccessType.EXCLUDE);

        List<String> includeEmails = includes.stream().map(EventEmailAccess::getEmail).toList();
        List<String> excludeEmails = excludes.stream().map(EventEmailAccess::getEmail).toList();

        IncludesExcludeEventMailAccessDTO includesExcludeEventMailAccessDTO = new IncludesExcludeEventMailAccessDTO();
        includesExcludeEventMailAccessDTO.setIncludes(includeEmails);
        includesExcludeEventMailAccessDTO.setExcludes(excludeEmails);
        includesExcludeEventMailAccessDTO.setOrganizationEventId(organizationEventId);

        return includesExcludeEventMailAccessDTO;
    }

    @Override
    @Transactional
    public IncludesExcludeEventMailAccessDTO createIncludesExcludes(IncludesExcludeEventMailAccessDTO includesExcludeEventMailAccessDTO) {
        List<String> includeEmails = includesExcludeEventMailAccessDTO.getIncludes();
        List<String> excludeEmails = includesExcludeEventMailAccessDTO.getExcludes();

        List<EventEmailAccess> includesResult = new ArrayList<>();
        List<EventEmailAccess> excludesResult = new ArrayList<>();

        if (!includeEmails.isEmpty()) {
            includesResult = includeEmails.stream().map(email -> {
                EventEmailAccess eventEmailAccess = new EventEmailAccess();

                OrganizationEvent event = organizationEventRepository.findById(includesExcludeEventMailAccessDTO.getOrganizationEventId()).orElseThrow(()-> new ApiRequestException("Event not found"));

                eventEmailAccess.setEmail(email);
                eventEmailAccess.setAccessType(EventEmailAccess.AccessType.INCLUDE);
                eventEmailAccess.setOrganizationEvent(event);

                return eventEmailAccessRepository.save(eventEmailAccess);
            }).toList();
        }

        if (!excludeEmails.isEmpty()) {
            excludesResult = excludeEmails.stream().map(email -> {
                EventEmailAccess eventEmailAccess = new EventEmailAccess();

                OrganizationEvent event = organizationEventRepository.findById(includesExcludeEventMailAccessDTO.getOrganizationEventId()).orElseThrow(()-> new ApiRequestException("Event not found"));
                eventEmailAccess.setEmail(email);
                eventEmailAccess.setAccessType(EventEmailAccess.AccessType.EXCLUDE);
                eventEmailAccess.setOrganizationEvent(event);

                return eventEmailAccessRepository.save(eventEmailAccess);
            }).toList();
        }

        IncludesExcludeEventMailAccessDTO result = new IncludesExcludeEventMailAccessDTO();
        result.setIncludes(includesResult.stream().map(EventEmailAccess::getEmail).toList());
        result.setExcludes(excludesResult.stream().map(EventEmailAccess::getEmail).toList());
        result.setOrganizationEventId(includesExcludeEventMailAccessDTO.getOrganizationEventId());

        return result;
    }

    @Override
    @Transactional
    public IncludesExcludeEventMailAccessDTO updateIncludesExcludes(IncludesExcludeEventMailAccessDTO includesExcludeEventMailAccessDTO) {
        // xóa dữ liệu cũ
        List<EventEmailAccess> oldData = eventEmailAccessRepository.findEventEmailAccessByOrganizationEventOrganizationEventId(includesExcludeEventMailAccessDTO.getOrganizationEventId());
        if (!oldData.isEmpty()) {
            oldData.forEach(eventEmailAccess -> {
                eventEmailAccessRepository.deleteById(eventEmailAccess.getEventEmailAccessId());
            });
        }

        // ghi lại
        List<String> includeEmails = includesExcludeEventMailAccessDTO.getIncludes();
        List<String> excludeEmails = includesExcludeEventMailAccessDTO.getExcludes();

        List<EventEmailAccess> includesResult = new ArrayList<>();
        List<EventEmailAccess> excludesResult = new ArrayList<>();

        if (!includeEmails.isEmpty()) {
            includesResult = includeEmails.stream().map(email -> {
                EventEmailAccess eventEmailAccess = new EventEmailAccess();

                OrganizationEvent event = organizationEventRepository.findById(includesExcludeEventMailAccessDTO.getOrganizationEventId()).orElseThrow(()-> new ApiRequestException("Event not found"));

                eventEmailAccess.setEmail(email);
                eventEmailAccess.setAccessType(EventEmailAccess.AccessType.INCLUDE);
                eventEmailAccess.setOrganizationEvent(event);

                return eventEmailAccessRepository.save(eventEmailAccess);
            }).toList();
        }

        if (!excludeEmails.isEmpty()) {
            excludesResult = excludeEmails.stream().map(email -> {
                EventEmailAccess eventEmailAccess = new EventEmailAccess();

                OrganizationEvent event = organizationEventRepository.findById(includesExcludeEventMailAccessDTO.getOrganizationEventId()).orElseThrow(()-> new ApiRequestException("Event not found"));
                eventEmailAccess.setEmail(email);
                eventEmailAccess.setAccessType(EventEmailAccess.AccessType.EXCLUDE);
                eventEmailAccess.setOrganizationEvent(event);

                return eventEmailAccessRepository.save(eventEmailAccess);
            }).toList();
        }

        IncludesExcludeEventMailAccessDTO result = new IncludesExcludeEventMailAccessDTO();
        result.setIncludes(includesResult.stream().map(EventEmailAccess::getEmail).toList());
        result.setExcludes(excludesResult.stream().map(EventEmailAccess::getEmail).toList());

        return result;
    }

    @Override
    @Transactional
    public void deleteIncludesExcludes(UUID organizationEventId) {
        List<EventEmailAccess> oldData = eventEmailAccessRepository.findEventEmailAccessByOrganizationEventOrganizationEventId(organizationEventId);
        if (!oldData.isEmpty()) {
            oldData.forEach(eventEmailAccess -> {
                eventEmailAccessRepository.deleteById(eventEmailAccess.getEventEmailAccessId());
            });
        }
    }

    @Override
    public List<User> getTargetUsersForSendingEventInvitationEmail(IncludesExcludeEventMailAccessDTO includesExcludeEventMailAccessDTO) {
        OrganizationEvent event = organizationEventRepository.findByOrganizationEventId(includesExcludeEventMailAccessDTO.getOrganizationEventId());
        List<String> includeEmails = includesExcludeEventMailAccessDTO.getIncludes();
        List<String> excludeEmails = includesExcludeEventMailAccessDTO.getExcludes();

        String targetAudienceGroups = event.getTargetAudienceGroups();
        List<User> groupAudiences = new ArrayList<>();

        if (targetAudienceGroups.contains("ALL")) {
            groupAudiences.addAll(userRepository.findAll());
            groupAudiences = groupAudiences.stream()
                    .filter(user -> !excludeEmails.contains(user.getEmail())).collect(Collectors.toList());
        } else {
            if (targetAudienceGroups.contains("MEMBER")) {
                groupAudiences.addAll(organizationMemberRepository
                        .findOrganizationMemberByMemberRole(OrganizationMember.OrganizationMemberRole.MEMBER)
                        .stream().map(OrganizationMember::getUser).toList());
            }

            if (targetAudienceGroups.contains("MANAGER")) {
                groupAudiences.addAll(organizationMemberRepository
                        .findOrganizationMemberByMemberRole(OrganizationMember.OrganizationMemberRole.MANAGER)
                        .stream().map(OrganizationMember::getUser).toList());
            }

            if (targetAudienceGroups.contains("CEO")) {
                groupAudiences.addAll(organizationMemberRepository
                        .findOrganizationMemberByMemberRole(OrganizationMember.OrganizationMemberRole.CEO)
                        .stream().map(OrganizationMember::getUser).toList());
            }

            groupAudiences = groupAudiences.stream()
                    .filter(user -> !excludeEmails.contains(user.getEmail())).collect(Collectors.toList());

            groupAudiences.addAll(includeEmails.stream()
                    .map(email -> userRepository.findByEmail(email).orElseThrow(() -> new ApiRequestException("User not found for sending event invitation email!"))).toList());
        }

        return groupAudiences;
    }

    @Override
    public void sendEventInvitationEmail(User targetUser, OrganizationEventDTO organizationEventDTO) {
        String email = targetUser.getEmail(), subject = "Tổ chức " + organizationEventDTO.getOrganizer().getOrganizationName() + " mời bạn tham gia sự kiện " + organizationEventDTO.getTitle();
        String organizationName = organizationEventDTO.getOrganizer().getOrganizationName(), eventTitle = organizationEventDTO.getTitle(), eventDetails = organizationEventDTO.getFullDescription();

        String htmlMessage = "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <title>Thư mời tham dự sự kiện từ thiện</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      font-family: 'Arial', 'Helvetica', sans-serif;\n" +
                "      background-color: #f4f4f4;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 20px auto;\n" +
                "      background-color: #ffffff;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "    .header {\n" +
                "      background-color: #000000;\n" +
                "      padding: 25px;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .header img {\n" +
                "      max-height: 80px;\n" +
                "      width: auto;\n" +
                "    }\n" +
                "    .content {\n" +
                "      padding: 35px;\n" +
                "      background-color: #ffffff;\n" +
                "    }\n" +
                "    .content h1 {\n" +
                "      font-size: 26px;\n" +
                "      font-weight: bold;\n" +
                "      color: #d81b60; /* Màu hồng đậm, phù hợp với từ thiện, có thể thay đổi */\n" +
                "      margin: 10px 0;\n" +
                "    }\n" +
                "    .content p {\n" +
                "      font-size: 16px;\n" +
                "      line-height: 1.7;\n" +
                "      color: #444;\n" +
                "      margin: 12px 0;\n" +
                "    }\n" +
                "    .event-details {\n" +
                "      margin: 25px 0;\n" +
                "      background-color: #fff8f8; /* Nền hồng nhạt */\n" +
                "      padding: 20px;\n" +
                "      border-radius: 6px;\n" +
                "      border-left: 4px solid #d81b60;\n" +
                "    }\n" +
                "    .event-details h2 {\n" +
                "      font-size: 18px;\n" +
                "      font-weight: bold;\n" +
                "      color: #d81b60;\n" +
                "      margin-bottom: 12px;\n" +
                "    }\n" +
                "    .event-info {\n" +
                "      display: flex;\n" +
                "      flex-wrap: wrap;\n" +
                "      gap: 25px;\n" +
                "      margin: 25px 0;\n" +
                "    }\n" +
                "    .event-info div {\n" +
                "      flex: 1;\n" +
                "      min-width: 200px;\n" +
                "    }\n" +
                "    .event-info p {\n" +
                "      font-size: 16px;\n" +
                "      margin: 10px 0;\n" +
                "      display: flex;\n" +
                "      align-items: center;\n" +
                "    }\n" +
                "    .event-info span.icon {\n" +
                "      font-size: 18px;\n" +
                "      margin-right: 10px;\n" +
                "      color: #d81b60;\n" +
                "    }\n" +
                "    .cta-button {\n" +
                "      display: inline-block;\n" +
                "      padding: 12px 30px;\n" +
                "      background-color: #d81b60;\n" +
                "      color: #ffffff;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 6px;\n" +
                "      font-size: 16px;\n" +
                "      font-weight: bold;\n" +
                "      margin: 20px 0;\n" +
                "      transition: background-color 0.3s;\n" +
                "    }\n" +
                "    .cta-button:hover {\n" +
                "      background-color: #b0003a;\n" +
                "    }\n" +
                "    .divider {\n" +
                "      border-top: 1px solid #eee;\n" +
                "      margin: 25px 0;\n" +
                "    }\n" +
                "    .participation {\n" +
                "      margin: 25px 0;\n" +
                "    }\n" +
                "    .participation h2 {\n" +
                "      font-size: 18px;\n" +
                "      font-weight: bold;\n" +
                "      color: #d81b60;\n" +
                "      margin-bottom: 12px;\n" +
                "    }\n" +
                "    .participation div {\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      background-color: #000000;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "      color: #ffffff;\n" +
                "      font-size: 14px;\n" +
                "    }\n" +
                "    .footer p {\n" +
                "      margin: 0;\n" +
                "      opacity: 0.9;\n" +
                "    }\n" +
                "    @media only screen and (max-width: 600px) {\n" +
                "      .container {\n" +
                "        width: 100%;\n" +
                "        margin: 10px;\n" +
                "      }\n" +
                "      .content {\n" +
                "        padding: 20px;\n" +
                "      }\n" +
                "      .event-info {\n" +
                "        flex-direction: column;\n" +
                "        gap: 15px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <!-- Header -->\n" +
                "    <div class=\"header\">\n" +
                "      <img\n" +
                "        src=\"https://res.cloudinary.com/dfoq1dvce/image/upload/v1738872035/rvgjtehnunvfb9ysw7sc.png\"\n" +
                "        alt=\"FCHARITY Logo\"\n" +
                "      />\n" +
                "    </div>\n" +
                "\n" +
                "    <!-- Content -->\n" +
                "    <div class=\"content\">\n" +
                "      <p style=\"font-size: 18px; font-weight: bold; color: #d81b60;\">\n" +
                "        Kính gửi "+ targetUser.getFullName() + ",\n" +
                "      </p>\n" +
                "      <p>\n" +
                "        <strong>" + organizationEventDTO.getOrganizer().getOrganizationName() + "</strong> trân trọng mời bạn cùng chung tay tham gia sự kiện từ thiện đầy ý nghĩa:\n" +
                "      </p>\n" +
                "      <h1>" + organizationEventDTO.getTitle() + "</h1>\n" +
                "      <p>\n" +
                "        Sự kiện này là cơ hội để chúng ta cùng nhau lan tỏa yêu thương và mang lại những thay đổi tích cực cho cộng đồng. Sự hiện diện của bạn sẽ là món quà quý giá đối với chúng tôi!\n" +
                "      </p>\n" +
                "\n" +
                "      <!-- Event Details -->\n" +
                "      <div class=\"event-details\">\n" +
                "        <h2>Về sự kiện</h2>\n" +
                "        <p>" + organizationEventDTO.getFullDescription() + "</p>\n" +
                "      </div>\n" +
                "\n" +
                "      <!-- Event Info with Windows Text Icons -->\n" +
                "      <div class=\"event-info\">\n" +
                "        <div>\n" +
                "          <p>\n" +
                "            <span class=\"icon\">\uD83D\uDD52</span>\n" +
                "            <strong>Thời gian bắt đầu:</strong> " + organizationEventDTO.getStartTime() + "\n" +
                "          </p>\n" +
                "          <p>\n" +
                "            <span class=\"icon\">⏰</span>\n" +
                "            <strong>Thời gian kết thúc:</strong> " + organizationEventDTO.getEndTime() + "\n" +
                "          </p>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "          <p>\n" +
                "            <span class=\"icon\">\uD83D\uDCCD</span>\n" +
                "            <strong>Địa điểm:</strong> " +organizationEventDTO.getLocation() + "\n" +
                "          </p>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "\n" +
                "      <div class=\"divider\"></div>\n" +
                "\n" +
                "      <!-- Participation Info -->\n" +
                "      <div class=\"participation\">\n" +
                "        <h2>Cách tham gia</h2>\n" +
                "        <div>\n" +
                "          <p><strong>Tham gia trực tuyến:</strong></p>\n" +
                "          <a style=\"color:white;\" href=\""+ organizationEventDTO.getMeetingLink() +"\" class=\"cta-button\">Tham gia ngay</a>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "          <p><strong>Tham gia trực tiếp:</strong></p>\n" +
                "          <p>" + organizationEventDTO.getLocation() + "</p>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "\n" +
                "      <p style=\"margin-top: 25px;\">\n" +
                "        Chúng tôi rất mong được chào đón bạn tại sự kiện này! Nếu bạn có bất kỳ câu hỏi hoặc cần thêm thông tin, xin vui lòng liên hệ qua email <a href=\"mailto:"+ organizationEventDTO.getOrganizer().getEmail() + "\" style=\"color: #d81b60;\">support@fcharity.org</a> hoặc số điện thoại <a href=\"tel:" + organizationEventDTO.getOrganizer().getPhoneNumber() + "\" style=\"color: #d81b60;\">+84 20 1234 5678</a>.\n" +
                "      </p>\n" +
                "      <p>\n" +
                "        Trân trọng,<br />\n" +
                "        <strong>Đội ngũ FCHARITY</strong>\n" +
                "      </p>\n" +
                "    </div>\n" +
                "\n" +
                "    <!-- Footer -->\n" +
                "    <div class=\"footer\">\n" +
                "      <p>© 2025 FCHARITY. Cùng nhau lan tỏa yêu thương.</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";

        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
