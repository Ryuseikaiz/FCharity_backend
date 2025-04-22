package fptu.fcharity.controller.manage.post;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component // Thêm annotation này
public class JwtUtil {
    public UUID extractUserId(String token) {
        // Implement logic giải mã token
        return UUID.randomUUID(); // Ví dụ tạm thời
    }
}
