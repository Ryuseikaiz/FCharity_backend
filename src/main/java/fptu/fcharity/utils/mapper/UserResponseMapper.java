package fptu.fcharity.utils.mapper;

import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserResponseMapper extends BaseMapper<User, UserResponse> {
    UserResponse toDTO(User entity);
    User toEntity(UserResponse dto);
}
