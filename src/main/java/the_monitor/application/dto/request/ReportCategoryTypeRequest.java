package the_monitor.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCategoryTypeRequest {

    // 유형(자사, 경쟁사, 고객사)별 카테고리 - 기사 리스트
    @JsonProperty("SELF")
    private List<ReportCategoryRequest> reportCategorySelfRequests = new ArrayList<>();
    @JsonProperty("COMPETITOR")
    private List<ReportCategoryRequest> reportCategoryCompetitorRequests = new ArrayList<>();
    @JsonProperty("INDUSTRY")
    private List<ReportCategoryRequest> reportCategoryIndustryRequests = new ArrayList<>();

    @Builder
    public ReportCategoryTypeRequest(List<ReportCategoryRequest> reportCategorySelfRequests, List<ReportCategoryRequest> reportCategoryCompetitorRequests, List<ReportCategoryRequest> reportCategoryIndustryRequests) {
        this.reportCategorySelfRequests = reportCategorySelfRequests;
        this.reportCategoryCompetitorRequests = reportCategoryCompetitorRequests;
        this.reportCategoryIndustryRequests = reportCategoryIndustryRequests;
    }

}
