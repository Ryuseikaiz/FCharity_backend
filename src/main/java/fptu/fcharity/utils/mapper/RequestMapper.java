package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.Request;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDto toDto(Request request);
    Request toEntity(RequestDto requestDto);
}