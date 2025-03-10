package fptu.fcharity.utils.mapper;

import fptu.fcharity.entity.Request;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import fptu.fcharity.response.request.RequestResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestResponseMapper extends BaseMapper<Request, RequestResponse>{
}
