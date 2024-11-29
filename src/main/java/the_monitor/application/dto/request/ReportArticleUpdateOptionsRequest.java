package the_monitor.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportArticleUpdateOptionsRequest {

    private boolean media;
    private boolean reporter;

    @Builder
    public ReportArticleUpdateOptionsRequest(boolean media,
                                             boolean reporter) {
        this.media = media;
        this.reporter = reporter;

    }

}
