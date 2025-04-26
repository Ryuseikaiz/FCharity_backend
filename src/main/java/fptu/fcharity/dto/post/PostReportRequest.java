package fptu.fcharity.dto.post;

import lombok.Data;

import java.util.UUID;

@Data
public class PostReportRequest {
    private UUID reporterId;
    private String reason;
}