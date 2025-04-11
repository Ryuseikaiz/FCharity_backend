package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, WalletMapper.class})
public interface OrganizationMapper {
    @Mapping(source = "ceo", target = "ceo")
    OrganizationDTO toDto(Organization entity);

    @Mapping(source = "ceo", target = "ceo")
    Organization toEntity(OrganizationDTO dto);
}
