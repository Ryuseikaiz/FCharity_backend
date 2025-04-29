package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.ProjectExtraFundRequestDTO;
import fptu.fcharity.entity.ProjectExtraFundRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface ProjectExtraFundRequestMapper {
    ProjectExtraFundRequestDTO toDTO(ProjectExtraFundRequest entity);
    ProjectExtraFundRequest toEntity(ProjectExtraFundRequestDTO dto);
}
