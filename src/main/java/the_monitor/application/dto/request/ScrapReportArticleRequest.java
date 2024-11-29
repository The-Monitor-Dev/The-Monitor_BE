package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ScrapArticleDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScrapReportArticleRequest {

    private List<ScrapArticleDto> reportArticles;

    @Builder
    public ScrapReportArticleRequest(List<ScrapArticleDto> reportArticles) {
        this.reportArticles = reportArticles;
    }

}
