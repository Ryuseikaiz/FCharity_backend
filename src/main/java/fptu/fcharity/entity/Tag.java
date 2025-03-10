package fptu.fcharity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "tag_id", nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "tag_name")
    private String tagName;

}