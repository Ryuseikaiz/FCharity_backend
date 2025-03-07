package fptu.fcharity.entity;

import fptu.fcharity.embeddable.ProjectUserRoleId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "project_user_roles")
@Getter
@Setter
public class ProjectUserRole {
    @EmbeddedId
    private ProjectUserRoleId id;

    @Column(name = "role_id")
    private UUID roleId;

    public ProjectUserRole() {
    }

    public ProjectUserRole(UUID userId, UUID projectId, UUID roleId) {
        this.id = new ProjectUserRoleId(userId, projectId);
        this.roleId = roleId;
    }

    public UUID getUserId() {
        return this.id.getUserId();
    }

    public UUID getProjectId() {
        return this.id.getProjectId();
    }

    public void setUserId(UUID userId) {
        this.id.setUserId(userId);
    }

    public void setProjectId(UUID projectId) {
        this.id.setProjectId(projectId);
    }
}
