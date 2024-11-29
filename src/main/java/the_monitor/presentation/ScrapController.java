package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.response.ScrapCategoryTypeResponse;
import the_monitor.application.service.ScrapService;
import the_monitor.common.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scrap")
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(summary = "스크랩하기", description = "기사를 스크랩하여 scrapId를 반환합니다.")
    @PostMapping()
    public ApiResponse<String> scrapArticle(@RequestParam("articleId") Long articleId) {

        return ApiResponse.onSuccessData("기사 스크랩 변경", scrapService.scrapArticle(articleId));

    }

    @Operation(summary = "스크랩한 기사 조회", description = "스크랩한 기사들을 조회합니다.")
    @GetMapping
    public ApiResponse<ScrapCategoryTypeResponse> getScrappedArticlesByClientId() {

        return ApiResponse.onSuccessData("스크랩 기사 조회 성공", scrapService.getScrappedArticlesByClientId());

    }

    @Operation(summary = "스크랩한 기사 상세 조회", description = "스크랩한 기사 하나의 상세 정보를 조회합니다.")
    @GetMapping("info")
    public ApiResponse<ScrapArticleDto> getScrapArticleInfo(@RequestParam("scrapId") Long scrapId) {

        return ApiResponse.onSuccessData("스크랩 기사 상세 조회 성공", scrapService.getScrapArticleInfo(scrapId));

    }

    @Operation(summary = "스크랩 취소", description = "스크랩한 기사를 취소합니다. (임시저장(X) 시 사용)")
    @PatchMapping("/unscrap")
    public ApiResponse<String> unScrapArticle() {

        return ApiResponse.onSuccess(scrapService.unScrapArticle());

    }

}
