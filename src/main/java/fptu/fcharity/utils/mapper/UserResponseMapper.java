package fptu.fcharity.utils.mapper;

import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseMapper extends BaseMapper<User, UserResponse> {
    @Mapping(source = "id", target = "userId")
    UserResponse toDTO(User entity);

    @Mapping(source = "userId", target = "id")
    User toEntity(UserResponse dto);
}