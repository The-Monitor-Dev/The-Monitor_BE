package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Report;
import the_monitor.domain.model.ReportArticle;
import the_monitor.domain.model.ReportCategory;

@Getter
@NoArgsConstructor
public class ReportArticleUpdateRequest {

    @NotBlank(message = "카테고리는 필수입니다.")
    private String categoryType;

    @NotBlank(message = "키워드는 필수입니다.")
    private String keyword;

    @NotBlank(message = "기사 제목은 필수입니다.")
    private String headLine;

    @NotBlank(message = "url은 필수입니다.")
    private String url;

    @NotBlank(message = "발행일자는 필수입니다.")
    private String publishedDate;

    private String media;

    private String reporter;


    @Builder
    public ReportArticleUpdateRequest(String categoryType,
                                      String keyword,
                                      String headLine,
                                      String url,
                                      String publishedDate,
                                      String media,
                                      String reporter) {

        this.categoryType = categoryType;
        this.keyword = keyword;
        this.headLine = headLine;
        this.url = url;
        this.publishedDate = publishedDate;
        this.media = media;
        this.reporter = reporter;

    }

    public ReportArticle toEntity() {
        return ReportArticle.builder()
                .categoryType(CategoryType.valueOf(categoryType))
                .title(headLine)
                .keyword(keyword)
                .url(url)
                .publishDate(publishedDate)
                .publisherName(media)
                .reporterName(reporter)
                .build();
    }

}
