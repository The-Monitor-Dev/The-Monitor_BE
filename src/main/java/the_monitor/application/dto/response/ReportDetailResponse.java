package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Category;
import the_monitor.domain.model.ReportArticle;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReportDetailResponse {

    private String title;
    private String color;
    private String logo;

    @JsonProperty("articles")
    private List<ReportCategoryTypeResponse> reportCategoryTypeResponses;

    @Builder
    public ReportDetailResponse(String title,
                                String color,
                                String logo,
                                List<ReportCategoryTypeResponse> reportCategoryTypeResponses) {

        this.title = title;
        this.color = color;
        this.logo = logo;
        this.reportCategoryTypeResponses = reportCategoryTypeResponses;

    }

}
