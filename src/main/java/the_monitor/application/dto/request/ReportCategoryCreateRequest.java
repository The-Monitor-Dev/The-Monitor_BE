package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Report;
import the_monitor.domain.model.ReportCategory;

@Getter
@NoArgsConstructor
public class ReportCategoryCreateRequest {

    private String reportCategoryName;
    private String reportCategoryDescription;
    private String reportCategoryType;

    @Builder
    public ReportCategoryCreateRequest(String reportCategoryName,
                                       String reportCategoryDescription,
                                       String reportCategoryType) {

        this.reportCategoryName = reportCategoryName;
        this.reportCategoryDescription = reportCategoryDescription;
        this.reportCategoryType = reportCategoryType;

    }

    public ReportCategory toEntity(Report report) {
        return ReportCategory.builder()
                .name(reportCategoryName)
                .description(reportCategoryDescription)
                .categoryType(CategoryType.valueOf(reportCategoryType))
                .report(report)
                .build();

    }
}
