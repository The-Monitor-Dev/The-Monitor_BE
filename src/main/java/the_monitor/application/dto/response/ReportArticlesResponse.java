package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.ReportArticle;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReportArticlesResponse {

    private Map<CategoryType, List<ReportArticle>> reportArticles;

    @Builder
    public ReportArticlesResponse(Map<CategoryType, List<ReportArticle>> reportArticles) {

        this.reportArticles = reportArticles;

    }

}