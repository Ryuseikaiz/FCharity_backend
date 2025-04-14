package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.OrganizationRequestDTO;
import fptu.fcharity.entity.OrganizationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OrganizationMapper.class})
public interface OrganizationRequestMapper {
    @Mapping(source = "organization", target = "organization")
    @Mapping(source = "user", target = "user")
    OrganizationRequestDTO toDTO(OrganizationRequest organizationRequest);

    @Mapping(source = "organization", target = "organization")
    @Mapping(source = "user", target = "user")
    OrganizationRequest toEntity(OrganizationRequestDTO organizationRequestDTO);
}
