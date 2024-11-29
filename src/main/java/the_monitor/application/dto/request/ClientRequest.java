package the_monitor.application.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.List;
import java.util.Map;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class ClientRequest {
    @NotBlank(message = "클라이언트 이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "담당자 이름은 필수 항목입니다.")
    private String managerName;

    @Schema(description = "카테고리와 키워드 매핑", example = "{ \"SELF\": [\"keyword1\", \"keyword2\"], \"COMPETITOR\": [\"keyword3\"], \"INDUSTRY\": [\"keyword4\"] }")
    @NotNull(message = "카테고리 키워드는 필수 항목입니다.")
    private Map<CategoryType, List<String>> categoryKeywords;

    @NotEmpty(message = "메일 수신자는 필수 항목입니다.")
    private List<String> recipientEmails; // 수신자 이메일 (필수)

    private List<String> ccEmails; // 참조인 이메일 (선택)

    @Builder
    public ClientRequest(String name,
                         String managerName,
                         Map<CategoryType, List<String>> categoryKeywords,
                         List<String> recipientEmails,
                         List<String> ccEmails) {

        this.name = name;
        this.managerName = managerName;
        this.categoryKeywords = categoryKeywords;
        this.recipientEmails = recipientEmails;
        this.ccEmails = ccEmails;

    }

}