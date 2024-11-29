package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportUpdateTitleRequest {

    private String title;

    @Builder
    public ReportUpdateTitleRequest(String title) {
        this.title = title;
    }

}
