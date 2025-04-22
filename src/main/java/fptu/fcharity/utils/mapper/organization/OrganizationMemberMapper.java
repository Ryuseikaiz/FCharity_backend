package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.OrganizationMemberDTO;
import fptu.fcharity.entity.OrganizationMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OrganizationMapper.class})
public interface OrganizationMemberMapper {
    @Mapping(source = "organization", target = "organization")
    @Mapping(source = "user", target = "user")
    OrganizationMemberDTO toDTO(OrganizationMember organizationMember);

    @Mapping(source = "organization", target = "organization")
    @Mapping(source = "user", target = "user")
    OrganizationMember toEntity(OrganizationMemberDTO organizationMemberDTO);
}
