package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.OrganizationEventDTO;
import fptu.fcharity.entity.OrganizationEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrganizationMapper.class})
public interface OrganizationEventMapper {
    @Mapping(source = "organizer", target = "organizer")
    OrganizationEventDTO toDTO(OrganizationEvent entity);

    @Mapping(source = "organizer", target = "organizer")
    OrganizationEvent toEntity(OrganizationEventDTO dto);
}
