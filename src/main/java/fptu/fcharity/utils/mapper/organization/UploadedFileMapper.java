package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.UploadedFileDTO;
import fptu.fcharity.entity.UploadedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OrganizationMapper.class})
public interface UploadedFileMapper {
    @Mapping(source = "uploadedBy", target = "uploadedBy")
    @Mapping(source = "organization", target = "organization")
    UploadedFileDTO toDTO(UploadedFile uploadedFile);

    @Mapping(source = "uploadedBy", target = "uploadedBy")
    @Mapping(source = "organization", target = "organization")
    UploadedFile toEntity(UploadedFileDTO uploadedFileDTO);
}