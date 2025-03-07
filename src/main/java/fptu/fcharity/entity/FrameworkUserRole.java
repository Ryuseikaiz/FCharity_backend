package fptu.fcharity.entity;

import fptu.fcharity.embeddable.FrameworkUserRoleId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "framework_user_roles")
@Getter
@Setter
public class FrameworkUserRole {
    @EmbeddedId
    private FrameworkUserRoleId id;

    public FrameworkUserRole() {
    }

    public FrameworkUserRole(UUID userId, UUID roleId) {
        this.id = new FrameworkUserRoleId(userId, roleId);
    }

    public UUID getUserId() {
        return this.id.getUserId();
    }

    public UUID getRoleId() {
        return this.id.getRoleId();
    }

    public void setUserId(UUID userId) {
        this.id.setUserId(userId);
    }

    public void setRoleId(UUID roleId) {
        this.id.setRoleId(roleId);
    }

}
