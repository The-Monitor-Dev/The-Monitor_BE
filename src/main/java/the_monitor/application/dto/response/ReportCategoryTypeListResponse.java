package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCategoryTypeListResponse {

    @JsonProperty("SELF")
    private List<ReportCategoryListResponse> reportCategorySelfResponses;
    @JsonProperty("COMPETITOR")
    private List<ReportCategoryListResponse> reportCategoryCompetitorResponses;
    @JsonProperty("INDUSTRY")
    private List<ReportCategoryListResponse> reportCategoryIndustryResponses;

    @Builder
    public ReportCategoryTypeListResponse(List<ReportCategoryListResponse> reportCategorySelfResponses,
                                          List<ReportCategoryListResponse> reportCategoryCompetitorResponses,
                                          List<ReportCategoryListResponse> reportCategoryIndustryResponses) {

        this.reportCategorySelfResponses = reportCategorySelfResponses;
        this.reportCategoryCompetitorResponses = reportCategoryCompetitorResponses;
        this.reportCategoryIndustryResponses = reportCategoryIndustryResponses;

    }

}
