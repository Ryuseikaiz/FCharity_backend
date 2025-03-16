package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationDtoMapper extends BaseMapper<Organization, OrganizationDto> {
}
