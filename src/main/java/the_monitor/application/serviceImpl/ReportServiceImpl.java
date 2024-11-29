package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.*;
import the_monitor.application.dto.response.*;
import the_monitor.application.service.*;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.ArticleRepository;
import the_monitor.domain.repository.ReportArticleRepository;
import the_monitor.domain.repository.ReportCategoryRepository;
import the_monitor.domain.repository.ReportRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final ReportArticleRepository reportArticleRepository;

    private final AccountService accountService;
    private final ArticleService articleService;
    private final ClientService clientService;
    private final ScrapService scrapService;

    private final S3Service s3Service;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ArticleRepository articleRepository;

    // 보고서 목록 조회
    @Override
    public List<ReportListResponse> getReports() {

        Long clientId = getClientIdFromAuthentication();

        Client client = findClientById(clientId);

        return client.getReports().stream()
                .map(report -> ReportListResponse.builder()
                        .reportId(report.getId())
                        .title(report.getTitle())
                        .createdAt(report.getCreatedAt().format(FORMATTER))
                        .updatedAt(report.getUpdatedAt().format(FORMATTER))
                        .build())
                .collect(Collectors.toList());

    }

    // 보고서 생성
    @Override
    @Transactional
    public ReportCreateResponse createReports(ReportCreateRequest request, MultipartFile logo) {

        Long clientId = getClientIdFromAuthentication();

        Client client = findClientById(clientId);

        String logoUrl = getLogoUrl(logo, client.getLogo());

        Report report = reportRepository.save(request.toEntity(client, logoUrl));
        // 각 카테고리별로 ReportArticle 생성 및 저장
        createAndSaveReportArticlesByCategories(report, request);

        scrapService.unScrapArticle();

        updateAdded(request);

        return ReportCreateResponse.builder()
                .reportId(report.getId())
                .build();

    }

    // 보고서 삭제
    @Override
    @Transactional
    public String deleteReports(Long reportId) {

        Long clientId = getClientIdFromAuthentication();

        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), findByClientIdAndReportId(clientId, reportId));
        reportRepository.deleteById(reportId);
        return "보고서 삭제 성공";

    }

    // 보고서 상세보기
    @Override
    public ReportDetailResponse getReportDetail(Long reportId) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        return ReportDetailResponse.builder()
                .color(report.getColor())
                .logo(report.getLogo())
                .title(report.getTitle())
                .reportCategoryTypeResponses(List.of(buildCategoryTypeResponse(report.getReportCategories())))
                .build();

    }

    // 보고서 기사 수동 추가
    @Override
    @Transactional
    public String updateReportArticle(Long reportId, ReportArticleUpdateRequest request) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        ReportArticle reportArticle = request.toEntity();
        reportArticleRepository.save(reportArticle);

        setReportArticleDefaultCategory(report, reportArticle, CategoryType.valueOf(request.getCategoryType()));

        report.setUpdatedAt();

        return "보고서 기사 추가 완료";

    }

    // 보고서 기사 삭제
    @Override
    @Transactional
    public String deleteReportArticle(Long reportId, Long reportArticleId) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        reportArticleRepository.deleteById(reportArticleId);

        report.setUpdatedAt();
        return "보고서 기사 삭제 완료";
    }

    // 보고서 기사 한 줄 요약 수정
    @Override
    @Transactional
    public String updateReportArticleSummary(Long reportId, Long reportArticleId, ReportUpdateSummaryRequest request) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        ReportArticle reportArticle = reportArticleRepository.findById(reportArticleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._REPORT_ARTICLE_NOT_FOUND));

        validContentLength(request.getSummary());

        reportArticle.updateSummary(request.getSummary());

        report.setUpdatedAt();

        return "보고서 기사 요약 수정 완료";

    }

    // 보고서 제목 수정
    @Override
    @Transactional
    public String updateReportTitle(Long reportId, ReportUpdateTitleRequest request) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        report.updateTitle(request.getTitle());

        return "보고서 제목 수정 완료";

    }

    // 보고서 색상 수정
    @Override
    @Transactional
    public String updateReportColor(Long reportId, ReportUpdateColorRequest request) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        report.updateColor(request.getColor());

        return "보고서 색상 수정 완료";

    }

    // 보고서 로고 수정
    @Override
    @Transactional
    public String updateReportLogo(Long reportId, MultipartFile logo) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        String logoUrl = getLogoUrl(logo, report.getClient().getLogo());
        report.updateLogo(logoUrl);

        return "보고서 로고 수정 완료";

    }

    // 보고서 검색
    @Override
    public List<ReportListResponse> searchReport(ReportSearchTitleRequest request) {

        Long clientId = getClientIdFromAuthentication();

        List<Report> reports = reportRepository.findByClientIdAndTitleContaining(clientId, request.getSearchTitle());

        return reports.stream()
                .map(report -> ReportListResponse.builder()
                        .reportId(report.getId())
                        .title(report.getTitle())
                        .createdAt(report.getCreatedAt().format(FORMATTER))
                        .updatedAt(report.getUpdatedAt().format(FORMATTER))
                        .build())
                .collect(Collectors.toList());

    }

    // 보고서 카테고리 조회
    @Override
    public ReportCategoryTypeListResponse getReportCategoryList(Long reportId) {

        Long clientId = getClientIdFromAuthentication();

        // 1. Report 조회 및 권한 검증
        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        // 2. ReportCategory 조회 및 유형별 분류
        Map<CategoryType, List<ReportCategoryListResponse>> categoryMap = reportCategoryRepository.findByReportId(reportId).stream()
                .collect(Collectors.groupingBy(
                        ReportCategory::getCategoryType, // CategoryType 기준으로 그룹화
                        Collectors.mapping(this::buildCategoryListResponse, Collectors.toList()) // ReportCategory -> ReportCategoryListResponse 매핑
                ));

        // 3. ReportCategoryTypeListResponse 생성 및 반환
        return ReportCategoryTypeListResponse.builder()
                .reportCategorySelfResponses(categoryMap.getOrDefault(CategoryType.SELF, List.of()))
                .reportCategoryCompetitorResponses(categoryMap.getOrDefault(CategoryType.COMPETITOR, List.of()))
                .reportCategoryIndustryResponses(categoryMap.getOrDefault(CategoryType.INDUSTRY, List.of()))
                .build();

    }

    // 보고서 기사 카테고리 수정
    @Override
    @Transactional
    public String updateReportArticleCategory(Long reportId, Long reportArticleId, Long newCategoryId) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        ReportArticle reportArticle = findReportArticleById(reportArticleId);

        ReportCategory newCategory = findReportCategoryById(newCategoryId);

        reportArticle.updateCategory(newCategory);

        report.setUpdatedAt();

        return "보고서 기사 카테고리 수정 완료";

    }

    // 보고서 카테고리 삭제
    @Override
    @Transactional
    public String deleteReportCategory(Long reportId, Long categoryId) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        ReportCategory reportCategory = findReportCategoryByIdAndReportId(categoryId, reportId);

        List<ReportArticle> articles = reportCategory.getReportArticles();

        for (ReportArticle article : articles) {
            setReportArticleDefaultCategory(report, article, article.getCategoryType());
        }

        reportCategoryRepository.delete(reportCategory);

        report.setUpdatedAt();

        return "보고서 카테고리 삭제 완료";

    }

    // 보고서 카테고리 생성
    @Override
    @Transactional
    public String createReportCategory(Long reportId, ReportCategoryCreateRequest request) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        reportCategoryRepository.save(request.toEntity(report));

        report.setUpdatedAt();

        return "보고서 카테고리 생성 완료";

    }

    // 미디어 기자 수정
    @Override
    @Transactional
    public String updateReportArticleOptions(Long reportId, ReportArticleUpdateOptionsRequest request) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        report.updateReportOptions(request.isMedia(), request.isReporter());

        return "보고서 기사 미디어 기자 수정 완료";

    }

    // 보고서 미디어 기자 옵션 조회
    @Override
    public ReportOptionsResponse getReportOptions(Long reportId) {

        Long clientId = getClientIdFromAuthentication();

        Report report = findByClientIdAndReportId(clientId, reportId);
        validIsAccountAuthorizedForReport(getAccountFromId(getAccountId()), report);

        return ReportOptionsResponse.builder()
                .media(report.isMedia())
                .reporter(report.isReporter())
                .build();

    }

    private Long getAccountId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();

    }

    private Account findAccountById() {
        return accountService.findAccountById(getAccountId());
    }

    private Long getClientIdFromAuthentication() {
        Account account = findAccountById();
        return account.getSelectedClientId();
    }

    private Account getAccountFromId(Long accountId) {
        return accountService.findAccountById(accountId);
    }

    private Client findClientById(Long clientId) {
        return clientService.findClientById(clientId);
    }

    private ReportCategory findReportCategoryByIdAndReportId(Long reportCategoryId, Long reportId) {

        return reportCategoryRepository.findByIdAndReportId(reportCategoryId, reportId);

    }

    private ReportCategory findReportCategoryById(Long reportCategoryId) {
        return reportCategoryRepository.findById(reportCategoryId)
                .orElseThrow(() -> new ApiException(ErrorStatus._REPORT_CATEGORY_NOT_FOUND));
    }


    private ReportArticle findReportArticleById(Long reportArticleId) {
        return reportArticleRepository.findById(reportArticleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._REPORT_ARTICLE_NOT_FOUND));
    }

    // Client ID와 Report ID로 Report 조회
    private Report findByClientIdAndReportId(Long clientId, Long reportId) {
        return reportRepository.findReportByClientIdAndReportId(clientId, reportId);
    }

    private String getLogoUrl(MultipartFile logo, String clientLogo) {
        return (logo == null) ? clientLogo : s3Service.uploadFile(logo);
    }

    // Account가 보고서에 권한이 있는지 확인
    private void validIsAccountAuthorizedForReport(Account account, Report report) {
        if (!report.getClient().getAccount().equals(account)) {
            throw new ApiException(ErrorStatus._REPORT_FORBIDDEN);
        }
    }

    private void validContentLength(String content) {
        if (content.length() > 100)
            throw new ApiException(ErrorStatus._INVALID_REPORT_ARTICLE_SUMMARY_LENGTH);
    }

    // ReportCreateRequest로부터 ReportArticle 생성 및 저장
    private void createAndSaveReportArticlesByCategories(Report report, ReportCreateRequest request) {

        // ReportCategory 리스트 생성
        List<ReportCategory> reportCategoryList = new ArrayList<>();

        // 기본 카테고리 생성 및 저장
        ReportCategory selfDefaultCategory = reportCategoryRepository.save(setDefaultCategory(report, CategoryType.SELF.name()));
        ReportCategory competitorDefault = reportCategoryRepository.save(setDefaultCategory(report, CategoryType.COMPETITOR.name()));
        ReportCategory industryDefault = reportCategoryRepository.save(setDefaultCategory(report, CategoryType.INDUSTRY.name()));

        // 유형별 카테고리 처리
        ReportCategoryTypeRequest categoryTypeRequest = request.getReportCategoryTypeRequest();

        // SELF 유형 처리
        processCategoryType(report, categoryTypeRequest.getReportCategorySelfRequests(), CategoryType.SELF, reportCategoryList, selfDefaultCategory);

        // COMPETITOR 유형 처리
        processCategoryType(report, categoryTypeRequest.getReportCategoryCompetitorRequests(), CategoryType.COMPETITOR, reportCategoryList, competitorDefault);

        // INDUSTRY 유형 처리
        processCategoryType(report, categoryTypeRequest.getReportCategoryIndustryRequests(), CategoryType.INDUSTRY, reportCategoryList, industryDefault);

        // 추가된 ReportCategory 리스트를 Report에 연결
        report.addReportCategories(reportCategoryList);

        // 기본 카테고리를 Report에 추가
        report.addReportCategory(selfDefaultCategory);
        report.addReportCategory(competitorDefault);
        report.addReportCategory(industryDefault);

    }

    private void processCategoryType(Report report,
                                     List<ReportCategoryRequest> categoryRequests,
                                     CategoryType categoryType,
                                     List<ReportCategory> reportCategoryList,
                                     ReportCategory defaultCategory) {

        categoryRequests.forEach(categoryRequest -> {

            if (Objects.equals(categoryRequest.getReportCategoryName(), "default")) {

                List<ReportArticle> reportArticles = createReportArticles(categoryRequest, defaultCategory);

                defaultCategory.addReportArticles(reportArticles);

            } else {
                ReportCategory reportCategory = createReportCategory(report, categoryRequest, categoryType);

                List<ReportArticle> reportArticles = createReportArticles(categoryRequest, reportCategory);

                reportCategory.addReportArticles(reportArticles);

                reportCategoryList.add(reportCategory);
            }
        });

    }

    private ReportCategory createReportCategory(Report report, ReportCategoryRequest categoryRequest, CategoryType categoryType) {

        return categoryRequest.toEntity(report, categoryType);

    }

    private List<ReportArticle> createReportArticles(ReportCategoryRequest categoryRequest, ReportCategory reportCategory) {

        return categoryRequest.getArticleId().stream()
                .map(articleId -> {
                    return copyReportArticleFromArticle(articleId, reportCategory);
                })
                .collect(Collectors.toList());

    }

    private ReportArticle copyReportArticleFromArticle(Long articleId, ReportCategory reportCategory) {

        Article article = articleService.findArticleById(articleId);

        return ReportArticle.builder()
                .title(article.getTitle())
                .url(article.getUrl())
                .publisherName(article.getPublisherName())
                .reporterName(article.getReporterName())
                .publishDate(article.getPublishDate())
                .categoryType(reportCategory.getCategoryType())
                .reportCategory(reportCategory)
                .keyword(article.getKeyword().getKeyword())
                .build();

    }

    // 보고서 상세조회
    private ReportCategoryTypeResponse buildCategoryTypeResponse(List<ReportCategory> reportCategories) {

        // 유형별로 분류
        Map<CategoryType, List<ReportCategoryResponse>> categoryMap = reportCategories.stream()
                .collect(Collectors.groupingBy(
                        ReportCategory::getCategoryType, // CategoryType 기준으로 그룹화
                        Collectors.mapping(this::buildCategoryResponse, Collectors.toList()) // ReportCategory -> ReportCategoryResponse
                ));

        return ReportCategoryTypeResponse.builder()
                .reportCategorySelfResponses(categoryMap.getOrDefault(CategoryType.SELF, List.of()))
                .reportCategoryCompetitorResponses(categoryMap.getOrDefault(CategoryType.COMPETITOR, List.of()))
                .reportCategoryIndustryResponses(categoryMap.getOrDefault(CategoryType.INDUSTRY, List.of()))
                .build();

    }

    private ReportCategoryResponse buildCategoryResponse(ReportCategory category) {

        List<ReportArticlesResponse> articlesResponses = category.getReportArticles().stream()
                .map(this::buildArticlesResponse)
                .collect(Collectors.toList());

        return ReportCategoryResponse.builder()
                .reportCategoryId(category.getId())
                .reportCategoryName(category.getName())
                .reportCategoryDescription(category.getDescription())
                .reportArticlesResponses(articlesResponses)
                .isDefault(category.isDefault())
                .build();

    }

    private ReportArticlesResponse buildArticlesResponse(ReportArticle article) {

        return ReportArticlesResponse.builder()
                .ReportArticleId(article.getId())
                .keyword(article.getKeyword())
                .publishedDate(article.getPublishDate() != null ? article.getPublishDate() : null)
                .headLine(article.getTitle())
                .url(article.getUrl())
                .media(article.getPublisherName())
                .reporter(article.getReporterName())
                .summary(article.getSummary())
                .build();

    }

    private ReportCategoryListResponse buildCategoryListResponse(ReportCategory category) {

        return ReportCategoryListResponse.builder()
                .reportCategoryId(category.getId())
                .reportCategoryName(category.getName())
                .reportCategoryDescription(category.getDescription())
                .isDefault(category.isDefault())
                .build();

    }

    private ReportCategory setDefaultCategory(Report report, String categoryType) {

        return ReportCategory.builder()
                .categoryType(CategoryType.valueOf(categoryType))
                .name("default")
                .description("기본 카테고리입니다.")
                .report(report)
                .isDefault(true)
                .build();
    }

    private void setReportArticleDefaultCategory(Report report, ReportArticle reportArticle, CategoryType categoryType) {

        ReportCategory defaultCategory = findReportCategoryDefault(report, categoryType);

        defaultCategory.addReportArticle(reportArticle);

        report.addReportCategory(defaultCategory);
        reportRepository.save(report);


    }

    private void updateAdded(ReportCreateRequest request) {

        List<Long> articleIds = getArticleIdsByReportCreateRequest(request);

        for (Long articleId : articleIds) {
            Article article = articleService.findArticleById(articleId);
            article.setAddedStatus(true);
            articleRepository.save(article);
        }

    }

    private List<Long> getArticleIdsByReportCreateRequest(ReportCreateRequest request) {

        // 각 카테고리의 articleId 리스트를 스트림으로 병합
        return Stream.concat(
                        Stream.concat(
                                request.getReportCategoryTypeRequest().getReportCategorySelfRequests().stream()
                                        .map(ReportCategoryRequest::getArticleId)
                                        .flatMap(Collection::stream),
                                request.getReportCategoryTypeRequest().getReportCategoryCompetitorRequests().stream()
                                        .map(ReportCategoryRequest::getArticleId)
                                        .flatMap(Collection::stream)
                        ),
                        request.getReportCategoryTypeRequest().getReportCategoryIndustryRequests().stream()
                                .map(ReportCategoryRequest::getArticleId)
                                .flatMap(Collection::stream)
                )
                .toList();

    }

    private ReportCategory findReportCategoryDefault(Report report, CategoryType categoryType) {
        return reportCategoryRepository.findByReportAndCategoryTypeAndName(report, categoryType, "default")
                .orElseThrow(() -> new ApiException(ErrorStatus._REPORT_CATEGORY_NOT_FOUND));
    }

}
