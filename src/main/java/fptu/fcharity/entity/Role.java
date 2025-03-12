package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {
    @Id
    @Column(name = "role_id", unique = true, updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    private UUID roleId;

    @Column(name = "name")
    private String name;
}
