package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportSearchTitleRequest {

    private String searchTitle;

    @Builder
    public ReportSearchTitleRequest(String searchTitle) {
        this.searchTitle = searchTitle;
    }

}
