package fptu.fcharity.dto.admindashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TagDTO {
    private UUID id;
    private String tagName;
}