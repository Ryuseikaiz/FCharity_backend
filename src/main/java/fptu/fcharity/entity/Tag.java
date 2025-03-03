package fptu.fcharity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "tag_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID tagId;

    @Column(name = "tag_name", nullable = false)
    private String tagName;
}
