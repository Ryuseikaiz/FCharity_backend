package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.dto.organization.VerificationDocumentDTO;
import fptu.fcharity.entity.OrganizationImage;
import fptu.fcharity.entity.User;

import fptu.fcharity.service.manage.organization.OrganizationImageService;
import fptu.fcharity.service.manage.organization.OrganizationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.constants.OrganizationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationRestController {
    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public OrganizationRestController(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    // Lấy danh sách tất cả các tổ chức triên hệ thống để show cho user và guest xem
    @GetMapping("/organizations")
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationService.findAll();
    }


    // Lấy danh sách các tổ chức mà người đang đăng nhập tham gia với vai trò thành viên
    @GetMapping("organizations/joined-organizations")
    public List<OrganizationDTO> getMyOrganizations() {
        return organizationService.getMyOrganizations();
    }

    // Lấy danh sách các tổ chức chờ xét duyệt để hoạt động
    @GetMapping("/organizations/admin-review/waiting-for-creation")
    public List<OrganizationDTO> getOrganizationsWaitingForCreation() {
        return organizationService.findAll().stream()
                .filter(organizationDTO -> Objects.equals(organizationDTO.getOrganizationStatus(), OrganizationStatus.PENDING)).toList();
    }

    // Lấy danh sách các tổ chức chờ xét duyệt để xóa
    @GetMapping("/organizations/admin-review/waiting-for-deletion")
    public List<OrganizationDTO> getOrganizationsWaitingForDeletion() {
        return organizationService.findAll().stream()
                .filter(organizationDTO -> Objects.equals(organizationDTO.getOrganizationStatus(), OrganizationStatus.WATINGFORDELETION)).toList();
    }

    // Lấy thông tin của một tổ chức có id nhất định
    @GetMapping("/organizations/{organizationId}")
    public OrganizationDTO getOrganizationById(@PathVariable UUID organizationId) {
        System.out.println("Get organization by id: " + organizationId);
        return organizationService.findById(organizationId);
    }

    // Tạo mới tổ chức cho người đang đang nhập
    @PostMapping("/organizations")  // OK
    public OrganizationDTO postOrganization(@RequestBody OrganizationDTO organizationDTO) throws IOException {
        System.out.println("🤖🤖🤖creating organization: " + organizationDTO);
        return organizationService.createOrganization(organizationDTO);
    }

    // Cập nhật thông tin cho tổ chức
    @PutMapping("/organizations")   // OK
    public OrganizationDTO putOrganization(@RequestBody OrganizationDTO organizationDTO) throws IOException {
        OrganizationDTO result = organizationService.updateOrganization(organizationDTO);
        System.out.println("🍎🍎Update organization: " + result);
        return result;
    }

    // Xóa (vô hiệu hóa) tổ chức và chờ Admin phê duyệt
    @DeleteMapping("/organizations/{organizationId}")
    public UUID deleteOrganization(@PathVariable UUID organizationId) {
        organizationService.deleteOrganizationByCeo(organizationId);
        return organizationId;
    }

    // Xóa tổ chức do Admin thực hiện
    @DeleteMapping("/organizations/admin-review/{organizationId}")
    public UUID deleteOrganizationByAdmin(@PathVariable UUID organizationId) {
        organizationService.deleteOrganizationByAdmin(organizationId);
        return organizationId;
    }

    // Lấy thông tin tổ chức do người đang đăng nhập làm Ceo
    @GetMapping("/organization/managedByCeo")
    public ResponseEntity<OrganizationDTO> getManagedOrganizationByCeo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser  = userService.findUserByEmail(authentication.getName()); // email

        if (currentUser != null) {
            UUID currentUserId = currentUser.getId();
            OrganizationDTO organization = organizationService.getOrganizationByCeoId(currentUserId);
            System.out.println("🦔 organizations: " + organization);
            if (organization == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(organization);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Lấy danh sách các tổ chức mà người đang đăng nhập nắm vai trò Manager
    @GetMapping("/organizations/managedByManager")
    public ResponseEntity<List<OrganizationDTO>> getManagedOrganizationsByManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser  = userService.findUserByEmail(authentication.getName()); // email

        if (currentUser != null) {
            UUID currentUserId = currentUser.getId();
            List<OrganizationDTO> organizations = organizationService.getOrganizationsByManagerId(currentUserId);
            System.out.println("🦔 organizations: " + organizations);
            if (organizations == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(organizations);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}