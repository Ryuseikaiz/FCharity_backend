package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Tag {
    @Id
    @Column(name = "tag_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID tagId;

    @Column(nullable = false)
    private String tagName;
}