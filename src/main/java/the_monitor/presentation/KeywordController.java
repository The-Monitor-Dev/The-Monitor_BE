package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.KeywordUpdateRequest;
import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.application.service.KeywordService;
import the_monitor.common.ApiResponse;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 리스트 반환", description = "clientId에 따른 키워드를 반환합니다.")
    @GetMapping()
    public ApiResponse<List<KeywordResponse>> getKeywords(@RequestParam("clientId") Long clientId) {
        return ApiResponse.onSuccessData("keyword 리스트", keywordService.getKeywords(clientId));
    }

    @PutMapping()
    @Operation(summary = "키워드 업데이트", description = "clientId에 따른 키워드를 업데이트합니다.")
    public ApiResponse<List<KeywordResponse>> updateKeywords(
            @RequestParam("clientId") Long clientId,
            @RequestBody KeywordUpdateRequest keywordUpdateRequest) {

        return ApiResponse.onSuccessData("키워드 업데이트 성공", keywordService.updateKeywords(clientId, keywordUpdateRequest));
    }
}