package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Keyword;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ScrapReportArticeResponse {

    private List<Long> scrapIds;

    @Builder
    public ScrapReportArticeResponse(List<Long> scrapIds) {
        this.scrapIds = scrapIds;
    }

}
