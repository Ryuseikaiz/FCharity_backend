package fptu.fcharity.dto.organization;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UploadedFileDTO {
    private UUID uploadedFileId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Instant uploadDate;
    private UserDTO uploadedBy;
    private OrganizationDTO organization;
    private Long fileSize;
}
