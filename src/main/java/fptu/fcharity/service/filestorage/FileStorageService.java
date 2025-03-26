package fptu.fcharity.service.filestorage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import org.springframework.util.Base64Utils;
import java.util.Base64;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String storeBase64Image(String base64Image) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        // Tách phần header (data:image/jpeg;base64,) và lấy dữ liệu base64
        String[] parts = base64Image.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];

        // Giải mã base64 thành bytes
        byte[] decodedBytes = Base64.getDecoder().decode(imageData);

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID().toString() + ".jpg"; // Hoặc .png tùy loại ảnh
        String relativePath = "/uploads/" + fileName;

        // Đảm bảo thư mục uploads tồn tại
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // Lưu file vào thư mục
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, decodedBytes);

        return relativePath; // Trả về đường dẫn tương đối
    }
}