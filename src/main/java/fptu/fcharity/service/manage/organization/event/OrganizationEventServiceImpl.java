package fptu.fcharity.service.manage.organization.event;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationEvent;
import fptu.fcharity.helpers.email.EmailService;
import fptu.fcharity.repository.manage.organization.OrganizationEventRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.OrganizationEventMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationEventServiceImpl implements OrganizationEventService {
    private final OrganizationEventRepository organizationEventRepository;
    private final OrganizationEventMapper organizationEventMapper;
    private final OrganizationRepository organizationRepository;
    private final EmailService emailService;

    @Autowired
    public OrganizationEventServiceImpl(OrganizationEventRepository organizationEventRepository, OrganizationEventMapper organizationEventMapper, OrganizationRepository organizationRepository, EmailService emailService) {
        this.organizationEventRepository = organizationEventRepository;
        this.organizationEventMapper = organizationEventMapper;
        this.organizationRepository = organizationRepository;
        this.emailService = emailService;
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

        return organizationEventMapper.toDTO(organizationEventRepository.save(event));
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

        System.out.println("update in service: 🍎🍎 " + updatedOrganizationEventDTO);

        return organizationEventMapper.toDTO(organizationEventRepository.save(organizationEventMapper.toEntity(updatedOrganizationEventDTO)));
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
    public void deleteByOrganizationEventId(UUID organizationEventId) {
        organizationEventRepository.deleteById((organizationEventId));
    }

    @Override
    public void sendEventInvitationEmail() {
        String email = "", subject = "";
        String organizationName = "", eventTitle = "", eventDetails = "";

        String htmlMessage = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body style=\"min-height: 400px; font-family: Arial, sans-serif; margin: 0; padding: 0;\">\n" +
                "    <div style=\"height: fit-content; font-family: Arial, Helvetica, sans-serif; background-color: #f5f5f5;\">\n" +
                "      <div style=\"width: 90%; margin: 0 auto; background-color: white; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">\n" +
                "        <div style=\"background-color: black;\">\n" +
                "          <img src=\"https://res.cloudinary.com/dfoq1dvce/image/upload/v1738872035/rvgjtehnunvfb9ysw7sc.png\" alt=\"logo\" style=\"padding: 5px 15px; height: 80px; width: 100px;\">\n" +
                "        </div>\n" +
                "        <div style=\"padding: 30px 40px 50px 40px;\">\n" +
                "          <p style=\"font-size: 28px; padding-top: 20px; color: black;\">" + organizationName +" cordially invites you to:</p>\n" +
                "          <p style=\"font-size: 24px; color: #007BFF; font-weight: bold;\">"+ eventTitle + "</p>\n" +
                "          <p style=\"padding-left: 20px;\">" + eventDetails + "</p>\n" +
                "          <br/>\n" +
                "          <hr style=\"border: 1px solid rgba(0, 0, 0, 0.1); margin-top: 16px;\" />\n" +
                "          <br/>\n" +
                "          <div style=\"padding-right: 2rem; padding-top: 16px;\">\n" +
                "            <p style=\"font-size: 21px; color: black;\">\n" +
                "              ⏰ <strong>Start Time:</strong> " + "[Day, Date and Time] " + " <br/><br/>\n" +
                "              \uD83D\uDCCD <strong>How to Join:</strong><br/>\n" +
                "              [If Online] \n" +
                "              <a href=\"[Google Meet Link]\" style=\"font-size: 18px; color: #007BFF; text-decoration: none;\">Click here to join online</a><br/>\n" +
                "              [If Offline] \n" +
                "              <span style=\"font-size: 18px;\">[Venue Name], [Full Address]</span>\n" +
                "            </p>\n" +
                "            <br/>\n" +
                "            <p style=\"font-size: 21px; color: black;\">\n" +
                "              We hope to see you at the event! If you have any questions, feel free to reach out to us.<br/><br/>\n" +
                "              Best regards,<br/>\n" +
                "              <strong>" + organizationName + "</strong>\n" +
                "            </p>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "        <!-- Footer -->\n" +
                "        <div style=\"background-color: black; padding: 13px 0px 49px 30px; display: flex; align-items: start;\">\n" +
                "          <p style=\"color: rgba(255, 255, 255, 0.929); text-align: left; font-size: 16px;\">© 2025 FCHARITY. All rights reserved.</p>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";
        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
