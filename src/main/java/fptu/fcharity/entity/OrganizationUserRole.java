package fptu.fcharity.entity;

import fptu.fcharity.embeddable.OrganizationUserRoleId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "organization_user_roles")
@Getter
@Setter
public class OrganizationUserRole {
    @EmbeddedId
    private OrganizationUserRoleId id;

    @Column(name = "role_id")
    private UUID roleId;

    public OrganizationUserRole() {
    }

    public OrganizationUserRole(UUID userId, UUID organizationId, UUID roleId) {
        this.id = new OrganizationUserRoleId(userId, organizationId);
        this.roleId = roleId;
    }

    public UUID getUserId() {
        return this.id.getUserId();
    }
    public UUID getOrganizationId() {
        return this.id.getOrganizationId();
    }

    public void setUserId(UUID userId) {
        this.id.setUserId(userId);
    }

    public void setOrganizationId(UUID organizationId) {
        this.id.setOrganizationId(organizationId);
    }
}
