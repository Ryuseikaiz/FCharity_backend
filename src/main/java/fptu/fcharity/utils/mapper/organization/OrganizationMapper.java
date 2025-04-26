package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.entity.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface OrganizationMapper {
    OrganizationDTO toDTO(Organization entity);
    Organization toEntity(OrganizationDTO dto);
}
