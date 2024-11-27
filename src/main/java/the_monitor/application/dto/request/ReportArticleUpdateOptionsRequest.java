package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportArticleUpdateOptionsRequest {

    private boolean isMedia;
    private boolean isReporter;

    @Builder
    public ReportArticleUpdateOptionsRequest(boolean isMedia,
                                             boolean isReporter) {
        this.isMedia = isMedia;
        this.isReporter = isReporter;

    }

}
