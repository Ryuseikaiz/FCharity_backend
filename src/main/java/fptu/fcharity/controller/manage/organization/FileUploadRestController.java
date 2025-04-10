package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.dto.organization.UploadedFileDTO;
import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.UploadedFile;
import fptu.fcharity.service.manage.organization.UploadedFileService;
import fptu.fcharity.utils.mapper.organization.UploadedFileMapper;
import fptu.fcharity.utils.mapper.organization.UploadedFileMapperImpl;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/files")
public class FileUploadRestController {
    private final UploadedFileService uploadedFileService;
    private final UploadedFileMapper uploadedFileMapper;

    public FileUploadRestController(UploadedFileService uploadedFileService, UploadedFileMapper uploadedFileMapper) {
        this.uploadedFileService = uploadedFileService;
        this.uploadedFileMapper = uploadedFileMapper;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        String projectRootDir = System.getProperty("user.dir");
        File projectDir = new File(projectRootDir);
        String parentDir = projectDir.getParent();

        String uploadDir = parentDir + "/uploads";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(uploadDir + filename);

        if (!file.exists() || file.isDirectory()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        MediaType mediaType = switch (fileExtension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "docx" -> MediaType.APPLICATION_OCTET_STREAM; // Trình duyệt sẽ tải hoặc mở bằng ứng dụng
            case "xlsx" -> MediaType.APPLICATION_OCTET_STREAM;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/organizations/{organizationId}")
    public List<UploadedFileDTO> getAllOrganizationDocuments(@PathVariable UUID organizationId) {
         return uploadedFileService.getAllByOrganizationId(organizationId).stream().map(uploadedFileMapper::toDTO).collect(Collectors.toList());
    }

    @PostMapping("/organizations/{organizationId}/save")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @PathVariable UUID organizationId) throws Exception {
        System.out.println("🕷️🕷️🕷️uploading file: " + file.getOriginalFilename());
        try {
            UploadedFile saveInfo = uploadedFileService.save(file, organizationId);

            UploadedFileDTO uploadedFileDTO = new UploadedFileDTO();

            uploadedFileDTO.setUploadedFileId(saveInfo.getUploadedFileId());
            uploadedFileDTO.setFileName(saveInfo.getFileName());
            uploadedFileDTO.setFilePath(saveInfo.getFilePath());
            uploadedFileDTO.setFileType(saveInfo.getFileType());
            uploadedFileDTO.setFileSize(saveInfo.getFileSize());
            uploadedFileDTO.setUploadDate(saveInfo.getUploadDate());

            UserDTO userDTO = new UserDTO();
            userDTO.setFullName(saveInfo.getUploadedBy().getFullName());
            userDTO.setEmail(saveInfo.getUploadedBy().getEmail());
            userDTO.setId(saveInfo.getUploadedBy().getId());

            OrganizationDTO organizationDTO = new OrganizationDTO();
            organizationDTO.setOrganizationName(saveInfo.getOrganization().getOrganizationName());
            organizationDTO.setOrganizationId(saveInfo.getOrganization().getOrganizationId());

            uploadedFileDTO.setOrganization(organizationDTO);

            System.out.println("File saved Info: " + saveInfo);

            return ResponseEntity.ok(uploadedFileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{uploadedFileId}")
    public ResponseEntity<?> delete(@PathVariable UUID uploadedFileId) throws Exception {
        boolean deleted = uploadedFileService.deleteFile(uploadedFileId);
        if (deleted) return ResponseEntity.ok("File deleted with Id: " + uploadedFileId);
        return ResponseEntity.status(404).body("File not found or cannot be deleted.");
    }

}