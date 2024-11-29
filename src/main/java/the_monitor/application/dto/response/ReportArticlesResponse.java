package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private Long reportArticleId;
    private String keyword;
    private String publishedDate;
    private String headLine;
    private String url;
    private String media;
    private String reporter;
    private String summary;

    @Builder
    public ReportArticlesResponse(Long ReportArticleId,
                                  String keyword,
                                  String publishedDate,
                                  String headLine,
                                  String url,
                                  String media,
                                  String reporter,
                                  String summary) {

        this.reportArticleId = ReportArticleId;
        this.keyword = keyword;
        this.publishedDate = publishedDate;
        this.headLine = headLine;
        this.url = url;
        this.media = media;
        this.reporter = reporter;
        this.summary = summary;

    }

}
