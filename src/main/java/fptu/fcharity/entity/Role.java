package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @Column(name = "role_id", unique = true, updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    private UUID role_id;

    @Column(name = "name")
    private String name;

    public Role() {
    }

    public Role(UUID role_id, String name) {
        this.role_id = role_id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "role_id=" + role_id +
                ", name='" + name + '\'' +
                '}';
    }
}
