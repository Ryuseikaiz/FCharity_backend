package fptu.fcharity.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadRestController {
    @Value("${upload.dir}")
    private String uploadDir;

    @PostMapping("/upload-cv")
    public ResponseEntity<?> uploadCV(@RequestParam("cv") MultipartFile file) {
        try {
            // Kiểm tra loại file
            String contentType = file.getContentType();
            if (!contentType.equals("application/pdf") &&
                    !contentType.equals("image/jpeg") &&
                    !contentType.equals("image/png")) {
                return ResponseEntity.badRequest().body("Only PDF, JPEG, or PNG files are allowed.");
            }

            // Tạo tên file duy nhất
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            String filePath = Paths.get(uploadDir, fileName).toString();

            // Đảm bảo thư mục uploads tồn tại
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Lưu file
            file.transferTo(new File(filePath));

            // Trả về đường dẫn tương đối
            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(new UploadResponse(fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }
}

class UploadResponse {
    private String fileUrl;

    public UploadResponse(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}