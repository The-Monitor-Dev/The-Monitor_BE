package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportCreateResponse {
    private Long reportId;

    @Builder
    public ReportCreateResponse(Long reportId) {
        this.reportId = reportId;
    }
}
