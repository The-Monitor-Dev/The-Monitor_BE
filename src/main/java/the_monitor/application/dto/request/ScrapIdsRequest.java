package the_monitor.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScrapIdsRequest {

    private List<Long> scrapIds;

    public ScrapIdsRequest(List<Long> scrapIds) {
        this.scrapIds = scrapIds;
    }

}
