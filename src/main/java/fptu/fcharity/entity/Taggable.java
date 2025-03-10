package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "taggable")
public class Taggable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(name = "taggable_id", nullable = false)
    private UUID taggableId;

    @Nationalized
    @Column(name = "taggable_type", nullable = false)
    private String taggableType;

    public Taggable(Tag tag, UUID taggableId, String taggableType) {
        this.tag = tag;
        this.taggableId = taggableId;
        this.taggableType = taggableType;
    }

}