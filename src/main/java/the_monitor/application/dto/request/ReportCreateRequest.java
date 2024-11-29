package the_monitor.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import the_monitor.domain.model.Client;
import the_monitor.domain.model.Report;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCreateRequest {

    private String reportTitle;
    private String color;
    private boolean media;
    private boolean reporter;

    @JsonProperty("articles")
    private ReportCategoryTypeRequest reportCategoryTypeRequest;

    @Builder
    public ReportCreateRequest(String reportTitle,
                               String color,
                               ReportCategoryTypeRequest reportCategoryTypeRequest,
                               boolean media,
                               boolean reporter) {

        this.reportTitle = reportTitle;
        this.color = color;
        this.reportCategoryTypeRequest = reportCategoryTypeRequest;
        this.media = media;
        this.reporter = reporter;

    }

    public Report toEntity(Client client, String logoUrl) {
        return Report.builder()
                .client(client)
                .title(reportTitle)
                .color(color)
                .logo(logoUrl)
                .media(media)
                .reporter(reporter)
                .build();
    }

}
