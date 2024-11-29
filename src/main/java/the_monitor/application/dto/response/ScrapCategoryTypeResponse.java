package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ScrapArticleDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScrapCategoryTypeResponse {

    @JsonProperty("SELF")
    private List<ScrapArticleDto> scrapSelfResponses;
    @JsonProperty("COMPETITOR")
    private List<ScrapArticleDto> scrapCompetitorResponses;
    @JsonProperty("INDUSTRY")
    private List<ScrapArticleDto> scrapIndustryResponses;

    @Builder
    public ScrapCategoryTypeResponse(List<ScrapArticleDto> scrapSelfResponses,
                                     List<ScrapArticleDto> scrapCompetitorResponses,
                                     List<ScrapArticleDto> scrapIndustryResponses) {

        this.scrapSelfResponses = scrapSelfResponses;
        this.scrapCompetitorResponses = scrapCompetitorResponses;
        this.scrapIndustryResponses = scrapIndustryResponses;

    }

}
