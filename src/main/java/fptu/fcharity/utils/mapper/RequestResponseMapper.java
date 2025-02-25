package fptu.fcharity.utils.mapper;


import fptu.fcharity.entity.Request;
import fptu.fcharity.response.request.RequestResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestResponseMapper extends BaseMapper<Request, RequestResponse>{
}
