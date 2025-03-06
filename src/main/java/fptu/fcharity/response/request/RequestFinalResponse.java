package fptu.fcharity.response.request;

import fptu.fcharity.entity.Request;
import fptu.fcharity.entity.Taggable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class RequestFinalResponse {
    private Request request;
    private List<Taggable> requestTags;
}
