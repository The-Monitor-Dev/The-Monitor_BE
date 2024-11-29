package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportUpdateSummaryRequest {

    private String summary;

    @Builder
    public ReportUpdateSummaryRequest(String summary) {
        this.summary = summary;
    }

}
