package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Report;
import the_monitor.domain.model.ReportCategory;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportCategoryRequest {

    private String reportCategoryName;
    private String reportCategoryDescription;
    private List<Long> articleId = new ArrayList<>();

    @Builder
    public ReportCategoryRequest(String reportCategoryName,
                                 String reportCategoryDescription,
                                 List<Long> articleId) {

        this.reportCategoryName = reportCategoryName;
        this.reportCategoryDescription = reportCategoryDescription;
        this.articleId = articleId;

    }

    public ReportCategory toEntity(Report report, CategoryType categoryType) {
        return ReportCategory.builder()
                .categoryType(categoryType)
                .name(reportCategoryName)
                .description(reportCategoryDescription)
                .report(report)
                .build();
    }

}
