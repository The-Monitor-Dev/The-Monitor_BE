package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCategoryTypeResponse {

    @JsonProperty("SELF")
    private List<ReportCategoryResponse> reportCategorySelfResponses;
    @JsonProperty("COMPETITOR")
    private List<ReportCategoryResponse> reportCategoryCompetitorResponses;
    @JsonProperty("INDUSTRY")
    private List<ReportCategoryResponse> reportCategoryIndustryResponses;

    @Builder
    public ReportCategoryTypeResponse(List<ReportCategoryResponse> reportCategorySelfResponses, List<ReportCategoryResponse> reportCategoryCompetitorResponses, List<ReportCategoryResponse> reportCategoryIndustryResponses) {
        this.reportCategorySelfResponses = reportCategorySelfResponses;
        this.reportCategoryCompetitorResponses = reportCategoryCompetitorResponses;
        this.reportCategoryIndustryResponses = reportCategoryIndustryResponses;
    }

}
