package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uploaded_file_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID uploadedFileId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "upload_date", nullable = false)
    private Instant uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", referencedColumnName = "user_id", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;
}
