package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCategoryResponse {

    private Long reportCategoryId;
    private String reportCategoryName;
    private String reportCategoryDescription;
    private boolean isDefault;

    private List<ReportArticlesResponse> reportArticlesResponses = new ArrayList<>();

    @Builder
    public ReportCategoryResponse(Long reportCategoryId,
                                  String reportCategoryName,
                                  String reportCategoryDescription,
                                  boolean isDefault,
                                  List<ReportArticlesResponse> reportArticlesResponses) {

        this.reportCategoryId = reportCategoryId;
        this.reportCategoryName = reportCategoryName;
        this.reportCategoryDescription = reportCategoryDescription;
        this.isDefault = isDefault;
        this.reportArticlesResponses = reportArticlesResponses;

    }

}
