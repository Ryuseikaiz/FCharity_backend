package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseMapper extends BaseMapper<User, UserResponse> {
}
