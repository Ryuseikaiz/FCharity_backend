package fptu.fcharity.dto.organization;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IncludesExcludeEventMailAccessDTO {
    private List<String> includes;
    private List<String> excludes;
    private UUID organizationEventId;
}
