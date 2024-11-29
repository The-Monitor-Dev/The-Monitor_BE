package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportOptionsResponse {

    private boolean media;
    private boolean reporter;

    @Builder
    public ReportOptionsResponse(boolean media,
                                 boolean reporter) {

        this.media = media;
        this.reporter = reporter;

    }

}
