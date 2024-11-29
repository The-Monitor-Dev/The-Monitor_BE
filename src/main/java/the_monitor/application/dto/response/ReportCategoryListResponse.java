package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportCategoryListResponse {

    public Long reportCategoryId;
    public String reportCategoryName;
    public String reportCategoryDescription;
    public boolean isDefault;

    @Builder
    public ReportCategoryListResponse(Long reportCategoryId,
                                      String reportCategoryName,
                                      String reportCategoryDescription,
                                      boolean isDefault) {

        this.reportCategoryId = reportCategoryId;
        this.reportCategoryName = reportCategoryName;
        this.reportCategoryDescription = reportCategoryDescription;
        this.isDefault = isDefault;

    }

}
