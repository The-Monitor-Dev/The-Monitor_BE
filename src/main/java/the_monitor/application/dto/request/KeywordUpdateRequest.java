package the_monitor.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class KeywordUpdateRequest {
    @Schema(description = "카테고리와 키워드 매핑", example = "{ \"SELF\": [\"keyword1\", \"keyword2\"], \"COMPETITOR\": [\"keyword3\"], \"INDUSTRY\": [\"keyword4\"] }")
    private Map<CategoryType, List<String>> keywordsByCategory;
}
