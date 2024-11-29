package the_monitor.application.service;

import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.*;
import the_monitor.application.dto.response.*;

import java.util.List;

public interface ReportService {

    List<ReportListResponse> getReports();

    ReportCreateResponse createReports(ReportCreateRequest request, MultipartFile logo);

    String deleteReports(Long reportId);

    ReportDetailResponse getReportDetail(Long reportId);

    String updateReportArticle(Long reportId, ReportArticleUpdateRequest request);

    String deleteReportArticle(Long reportId, Long reportArticleId);

    String updateReportArticleSummary(Long reportId, Long reportArticleId, ReportUpdateSummaryRequest request);

    String updateReportTitle(Long reportId, ReportUpdateTitleRequest request);

    String updateReportColor(Long reportId, ReportUpdateColorRequest request);

    String updateReportLogo(Long reportId, MultipartFile logo);

    List<ReportListResponse> searchReport(ReportSearchTitleRequest request);

    ReportCategoryTypeListResponse getReportCategoryList(Long reportId);

    String updateReportArticleCategory(Long reportId, Long reportArticleId, Long newCategoryId);

    String deleteReportCategory(Long reportId, Long categoryId);

    String createReportCategory(Long reportId, ReportCategoryCreateRequest request);

    String updateReportArticleOptions(Long reportId, ReportArticleUpdateOptionsRequest request);

    ReportOptionsResponse getReportOptions(Long reportId);

}
