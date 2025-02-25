package fptu.fcharity.utils.mapper;

import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", implementationName = "customUserResponseMapperImpl")
public interface UserResponseMapper extends BaseMapper<User, UserResponse> {
}

