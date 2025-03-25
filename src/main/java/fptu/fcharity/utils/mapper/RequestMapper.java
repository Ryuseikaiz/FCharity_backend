package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.HelpRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDto toDto(HelpRequest request);
    HelpRequest toEntity(RequestDto requestDto);
}