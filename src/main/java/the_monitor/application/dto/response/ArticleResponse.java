package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ArticleGoogleDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleResponse {

    private List<ArticleGoogleDto> googleArticles;
    private int totalResults;
    private String keyword;

    @Builder
    public ArticleResponse(List<ArticleGoogleDto> googleArticles,
                           int totalResults,
                           String keyword) {

        this.googleArticles = googleArticles;
        this.totalResults = totalResults;
        this.keyword = keyword;

    }

}
