package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.EventEmailAccessDTO;
import fptu.fcharity.entity.EventEmailAccess;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OrganizationEventMapper.class})
public interface EventEmailAccessMapper {
    EventEmailAccessDTO toDTO(EventEmailAccess eventEmailAccess);
    EventEmailAccess toEntity(EventEmailAccessDTO eventEmailAccessDTO);
}
