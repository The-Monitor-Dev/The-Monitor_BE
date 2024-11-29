package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleSearchRequest {

    @NotBlank(message = "검색기간은 필수입니다.")
    private String dateRestrict;

    @NotBlank(message = "키워드는 필수입니다.")
    private String keyword;

    @NotBlank(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "page 번호는 필수입니다.")
    private int page;

    @NotBlank(message = "page 당 보여질 갯수는 필수입니다.")
    private int size;



}
