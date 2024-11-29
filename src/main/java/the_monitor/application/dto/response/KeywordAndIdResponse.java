package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

@Getter
@NoArgsConstructor
public class KeywordAndIdResponse {

    private Long keywordId;
    private String keywordName;
    private CategoryType categoryType;

    @Builder
    public KeywordAndIdResponse(Long keywordId,
                                String keywordName,
                                CategoryType categoryType) {

        this.keywordId = keywordId;
        this.keywordName = keywordName;
        this.categoryType = categoryType;

    }

}
