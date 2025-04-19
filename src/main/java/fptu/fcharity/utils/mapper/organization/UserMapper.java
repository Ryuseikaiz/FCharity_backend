package fptu.fcharity.utils.mapper.organization;

import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);
}
