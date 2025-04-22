package fptu.fcharity.service.manage.organization;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.UploadedFile;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.UploadedFileRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.manage.user.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UploadedFileService {
    private final String projectRootDir;
    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final List<String> contentTypes = new ArrayList<>();

    public UploadedFileService(UploadedFileRepository uploadedFileRepository, UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;

        projectRootDir = System.getProperty("user.dir");

        contentTypes.add("application/vnd.ms-excel");
        contentTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypes.add("text/csv");
        contentTypes.add("application/vnd.oasis.opendocument.spreadsheet");
        contentTypes.add("application/pdf");
        contentTypes.add("application/msword");

        contentTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypes.add("application/vnd.ms-powerpoint");
        contentTypes.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");

        contentTypes.add("image/jpeg");
        contentTypes.add("image/png");
        contentTypes.add("image/bmp");
        contentTypes.add("image/gif");

        contentTypes.add("application/zip");
        contentTypes.add("application/vnd.rar");

        contentTypes.add("video/mp4");
        contentTypes.add("audio/mpeg");

        contentTypes.add("text/plain");
    }

    public UploadedFile save(MultipartFile file, UUID organizationId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống hoặc null");
        }

        User user = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));

        String contentType = file.getContentType();
        if (contentType == null || !contentTypes.contains(contentType)) {
            throw new RuntimeException("Invalid content type");
        }

        UploadedFile newFile = new UploadedFile();

        File directory = new File(getUploadDir());

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileType =file.getContentType();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(getUploadDir(), fileName);

        file.transferTo(destination); // Ghi file trực tiếp
        String filePath = destination.getAbsolutePath();

        newFile.setFileName(fileName);
        newFile.setFilePath(filePath);
        newFile.setFileType(fileType);
        newFile.setUploadDate(Instant.now());
        newFile.setUploadedBy(user);
        newFile.setOrganization(organization);
        newFile.setFileSize(file.getSize());

        return uploadedFileRepository.save(newFile);
    }

    public List<UploadedFile> getAllByOrganizationId(UUID organizationId) {
        return uploadedFileRepository.findUploadedFileByOrganizationOrganizationId(organizationId);
    }

    public UploadedFile findOne(UUID id) {
        return uploadedFileRepository.findUploadedFileByUploadedFileId(id);
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (fileName.contains("..")) {
                return ResponseEntity.badRequest().build();
            }
            Path uploadPath = Paths.get(getUploadDir()).normalize();
            Path filePath = uploadPath.resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }
    public void delete(UUID id) {
        if (findOne(id) == null) {
            return;
        }
        uploadedFileRepository.delete(findOne(id));
    }

    public boolean deleteFile(UUID id) throws Exception {
        UploadedFile uploadedFile = findOne(id);
        Path filePath = Paths.get(getUploadDir(), uploadedFile.getFileName());

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            uploadedFileRepository.deleteUploadedFileByUploadedFileId(id);
            return true;
        }
        return false;
    }

    private String getUploadDir() {
        File projectDir = new File(projectRootDir);
        String parentDir = projectDir.getParent();
        return parentDir + "/uploads";
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
