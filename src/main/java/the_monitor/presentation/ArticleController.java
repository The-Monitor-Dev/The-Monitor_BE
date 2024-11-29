package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.ArticleService;
import the_monitor.common.ApiResponse;
import the_monitor.common.PageResponse;
import the_monitor.domain.enums.CategoryType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "전체 기사 출력", description = "카테고리별로 모든 키워드의 기사들을 출력합니다.")
    @GetMapping()
    public ApiResponse<PageResponse<ArticleResponse>> getArticles(@RequestParam("categoryType") CategoryType categoryType,
                                                                  @RequestParam("page") int page) {

        return ApiResponse.onSuccessData("전체 기사", articleService.getArticlesByClientAndCategoryType(categoryType, page));

    }

    @Operation(summary = "keyword 기사 출력", description = "keywordId에 해당하는 기사를 출력합니다.")
    @GetMapping("/keyword")
    public ApiResponse<PageResponse<ArticleResponse>> getArticlesByKeyword(@RequestParam("keywordId") Long keywordId,
                                                                          @RequestParam("categoryType") CategoryType categoryType,
                                                                          @RequestParam("page") int page) {

        return ApiResponse.onSuccessData("검색 기사", articleService.getArticlesByKeyword(categoryType, keywordId, page));

    }

    @Operation(summary = "기사 읽음 표시", description = "기사를 읽음 표시합니다.")
    @PatchMapping("/read")
    public ApiResponse<String> readArticle(@RequestParam("articleId") Long articleId) {

        return ApiResponse.onSuccess(articleService.readArticle(articleId));

    }

}
