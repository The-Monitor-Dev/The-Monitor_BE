package the_monitor.presentation;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.*;
import the_monitor.application.dto.response.*;
import the_monitor.application.service.ReportService;
import the_monitor.common.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "보고서 목록 최신순", description = "보고서 목록을 최신순으로 조회합니다.")
    @GetMapping()
    public ApiResponse<List<ReportListResponse>> getReports() {

        return ApiResponse.onSuccessData("보고서 목록", reportService.getReports());

    }

    @Operation(summary = "보고서 생성", description = "보고서를 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ReportCreateResponse> createReports(@RequestPart("request") ReportCreateRequest request,
                                                           @RequestPart(value = "logo", required = false) MultipartFile logo) {

        return ApiResponse.onSuccessData("보고서 생성 성공", reportService.createReports(request, logo));

    }

    @Operation(summary = "보고서 삭제", description = "보고서를 삭제합니다.")
    @DeleteMapping()
    public ApiResponse<String> deleteReports(@RequestParam("reportId") Long reportId) {

        return ApiResponse.onSuccess(reportService.deleteReports(reportId));

    }

    // Response 변경 요망
    @Operation(summary = "보고서 상세 조회", description = "보고서 상세 정보를 조회합니다.")
    @GetMapping("/details")
    public ApiResponse<ReportDetailResponse> getReportDetail(@RequestParam("reportId") Long reportId) {

        return ApiResponse.onSuccessData("보고서 상세 조회", reportService.getReportDetail(reportId));

    }

    // 수동 추가 시 Default로 넣어야 함.
    @Operation(summary = "보고서 기사 수동 추가", description = "보고서에 포함된 기사를 수동 추가합니다.")
    @PostMapping("/articles/update")
    public ApiResponse<String> updateReportArticle(@RequestParam("reportId") Long reportId,
                                                   @RequestBody @Valid ReportArticleUpdateRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportArticle(reportId, request));

    }

    @Operation(summary = "보고서 기사 삭제", description = "보고서에 포함된 기사를 삭제합니다.")
    @DeleteMapping("/articles/delete")
    public ApiResponse<String> deleteReportArticle(@RequestParam("reportId") Long reportId,
                                                   @RequestParam("reportArticleId") Long reportArticleId) {

        return ApiResponse.onSuccess(reportService.deleteReportArticle(reportId, reportArticleId));

    }

    @Operation(summary = "보고서 요약 수정", description = "보고서 요약을 수정합니다.")
    @PatchMapping("/articles/summary")
    public ApiResponse<String> updateReportArticleSummary(@RequestParam("reportId") Long reportId,
                                                          @RequestParam("reportArticleId") Long reportArticleId,
                                                          @RequestBody ReportUpdateSummaryRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportArticleSummary(reportId, reportArticleId, request));

    }

    @Operation(summary = "보고서 제목 수정", description = "보고서 제목을 수정합니다.")
    @PatchMapping(value = "/title")
    public ApiResponse<String> updateReportTitle(@RequestParam("reportId") Long reportId,
                                                 @RequestBody ReportUpdateTitleRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportTitle(reportId, request));

    }

    @Operation(summary = "보고서 색상 수정", description = "보고서 색상을 수정합니다.")
    @PatchMapping(value = "/color")
    public ApiResponse<String> updateReportColor(@RequestParam("reportId") Long reportId,
                                                 @RequestBody ReportUpdateColorRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportColor(reportId, request));

    }

    @Operation(summary = "보고서 로고 수정", description = "보고서 로고를 수정합니다.")
    @PatchMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> updateReportlogo(@RequestParam("reportId") Long reportId,
                                                @RequestPart(value = "logo", required = false) MultipartFile logo) {

        return ApiResponse.onSuccess(reportService.updateReportLogo(reportId, logo));

    }

    @Operation(summary = "보고서 검색", description = "보고서를 검색합니다.")
    @PostMapping("/search")
    public ApiResponse<List<ReportListResponse>> searchReport(@RequestBody ReportSearchTitleRequest request) {

        return ApiResponse.onSuccessData("보고서 검색", reportService.searchReport(request));

    }

    @Operation(summary = "보고서 카테고리 조회", description = "보고서 카테고리를 조회합니다.")
    @GetMapping("/categories")
    public ApiResponse<ReportCategoryTypeListResponse> getReportCategories(@RequestParam("reportId") Long reportId) {

        return ApiResponse.onSuccessData("보고서 카테고리 조회", reportService.getReportCategoryList(reportId));

    }

    @Operation(summary = "보고서 기사 카테고리 수정", description = "보고서에 포함된 기사의 카테고리를 수정합니다.")
    @PatchMapping("/articles/category")
    public ApiResponse<String> updateReportArticleCategory(@RequestParam("reportId") Long reportId,
                                                           @RequestParam("reportArticleId") Long reportArticleId,
                                                           @RequestParam("newCategoryId") Long newCategoryId) {

        return ApiResponse.onSuccess(reportService.updateReportArticleCategory(reportId, reportArticleId, newCategoryId));

    }

    @Operation(summary = "보고서 카테고리 삭제", description = "보고서 카테고리를 삭제합니다.")
    @PatchMapping("/category/delete")
    public ApiResponse<String> deleteReportCategory(@RequestParam("reportId") Long reportId,
                                                    @RequestParam("categoryId") Long categoryId) {

        return ApiResponse.onSuccess(reportService.deleteReportCategory(reportId, categoryId));

    }

    @Operation(summary = "보고서 카테고리 생성", description = "보고서 카테고리를 생성합니다.")
    @PostMapping("/category")
    public ApiResponse<String> createReportCategory(@RequestParam("reportId") Long reportId,
                                                    @RequestBody ReportCategoryCreateRequest request) {

        return ApiResponse.onSuccess(reportService.createReportCategory(reportId, request));

    }

    @Operation(summary = "보고서 기사 미디어 기자 변경", description = "보고서에 포함된 기사의 미디어 기자 여부를 변경합니다.")
    @PatchMapping("/articles/options")
    public ApiResponse<String> updateReportArticleOptions(@RequestParam("reportId") Long reportId,
                                                          @RequestBody ReportArticleUpdateOptionsRequest request) {

        return ApiResponse.onSuccess(reportService.updateReportArticleOptions(reportId, request));

    }

    @Operation(summary = "보고서 기사 옵션 조회", description = "보고서 기사 옵션을 조회합니다.")
    @GetMapping("/articles/options")
    public ApiResponse<ReportOptionsResponse> getReportOptions(@RequestParam("reportId") Long reportId) {

        return ApiResponse.onSuccessData("보고서 기사 옵션 조회", reportService.getReportOptions(reportId));

    }

}
