package fptu.fcharity.response.request;

import fptu.fcharity.entity.HelpRequest;
import fptu.fcharity.entity.Taggable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class RequestFinalResponse {
    private HelpRequestResponse helpRequest;
    private List<Taggable> requestTags;
    private List<String> attachments;
}
