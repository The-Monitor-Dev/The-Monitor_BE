package the_monitor.application.serviceImpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ReportArticleDto;
import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.request.ScrapIdsRequest;
import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ScrapArticleListResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.application.service.ClientService;
import the_monitor.application.service.ScrapService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Account;
import the_monitor.domain.model.Article;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Scrap;
import the_monitor.domain.repository.AccountRepository;
import the_monitor.domain.repository.ArticleRepository;
import the_monitor.domain.repository.ClientRepository;
import the_monitor.domain.repository.ScrapRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Getter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapServiceImpl implements ScrapService {

    private final ScrapRepository scrapRepository;
    private final ArticleRepository articleRepository;
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Override
    public Scrap findById(Long scrapId) {
        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new ApiException(ErrorStatus._SCRAP_NOT_FOUND));
    }

    @Override
    @Transactional
    public ScrapReportArticeResponse scrapArticle(Long articleId) {
        // 1. Client 인증 정보 가져오기
        Long clientId = getClientIdFromAuthentication();

        // 2. Client 조회
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        // 3. Article 조회 및 상태 업데이트
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ARTICLE_NOT_FOUND));

        // Article의 isScraped 필드 업데이트
        article.setScrapStatus(true);

        // 4. Scrap 엔티티 생성 및 저장
        Scrap scrap = Scrap.builder()
                .client(client)
                .title(article.getTitle())
                .url(article.getUrl())
                .keyword(article.getKeyword() != null ? article.getKeyword().getKeyword() : null)
                .publisherName(article.getPublisherName())
                .reporterName(article.getReporterName())
                .publishDate(article.getPublishDate())
                .categoryType(article.getKeyword() != null ? article.getKeyword().getCategory().getCategoryType() : null)
                .build();

        Scrap savedScrap = scrapRepository.save(scrap);

        // 5. 저장된 Scrap ID 반환
        return ScrapReportArticeResponse.builder()
                .scrapIds(List.of(savedScrap.getId()))
                .build();
    }

    @Override
    @Transactional
    public String unscrapArticle(Long articleId) {
        // 1. Client 인증 정보 가져오기
        Long clientId = getClientIdFromAuthentication();

        // 2. Client 조회
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        // 3. Article 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ARTICLE_NOT_FOUND));

        // 4. Scrap 조회 및 삭제
        Scrap scrap = scrapRepository.findByClientAndTitleAndUrl(client, article.getTitle(), article.getUrl())
                .orElseThrow(() -> new ApiException(ErrorStatus._SCRAP_NOT_FOUND));

        scrapRepository.delete(scrap);

        // 5. Article의 isScrapped 필드 업데이트
        article.setScrapStatus(false);

        return "스크랩 취소했습니다.";
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScrapArticleDto> getScrapedArticlesByClientId() {
        Long clientId = getClientIdFromAuthentication();

        // clientId가 해당 accountId에 속해 있는지 확인
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        // clientId와 isScraped = true 조건으로 Article 조회
        List<Article> scrapedArticles = articleRepository.findAllByKeyword_Category_Client_IdAndIsScrapedTrue(clientId);

        // 조회된 Article 데이터를 ScrapArticleDto로 변환
        return scrapedArticles.stream()
                .map(article -> ScrapArticleDto.builder()
                        .articleId(article.getId())
                        .title(article.getTitle())
                        .url(article.getUrl())
                        .publisherName(article.getPublisherName())
                        .reporterName(article.getReporterName())
                        .publishDate(article.getPublishDate())
                        .categoryType(article.getKeyword() != null ? article.getKeyword().getCategory().getCategoryType() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private Client findClientById(Long clientId) {
        return clientService.findClientById(clientId);
    }

    // Scrap -> ReportArticleDto 변환
//    private ReportArticleDto convertToReportArticleDto(Scrap scrap) {
//        return ReportArticleDto.builder()
//                .publishedDate(scrap.getPublishDate())
//                .keyword(scrap.getCategoryType().name())  // CategoryType을 문자열로 변환
//                .headLine(scrap.getTitle())
//                .url(scrap.getUrl())
//                .media(scrap.getPublisherName())
//                .reporter(scrap.getReporterName())
//                .build();
//    }
    private Long getAccountId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();

    }

    private Account findAccountById() {
        return accountRepository.findAccountById(getAccountId());
    }

    private Long getClientIdFromAuthentication() {
        Account account = findAccountById();
        return account.getSelectedClientId();
    }
}

//    private Client findClientById(Long clientId) {
//        return clientService.findClientById(clientId);
//    }
//
//    // Article -> Scrap 저장
//    private List<Scrap> convertAndSaveScrap(List<ScrapArticleDto> reportArticles, Client client) {
//
//        return reportArticles.stream()
//                .map((articleDto) -> {
//                    Scrap scrap = Scrap.builder()
//                            .client(client)
//                            .categoryType(articleDto.getCategoryType())
//                            .title(articleDto.getTitle())
//                            .url(articleDto.getUrl())
//                            .publishDate(articleDto.getPublishDate())
//                            .publisherName(articleDto.getPublisherName())
//                            .reporterName(articleDto.getReporterName())
//                            .build();
//                    return scrapRepository.save(scrap);
//                })
//                .collect(Collectors.toList());
//
//    }

//    // Scrap -> ReportArticleDto 변환
//    private ReportArticleDto convertToReportArticleDto(Scrap scrap) {
//        return ReportArticleDto.builder()
//                .publishedDate(scrap.getPublishDate())
//                .keyword(scrap.getCategoryType().name())  // CategoryType을 문자열로 변환
//                .headLine(scrap.getTitle())
//                .url(scrap.getUrl())
//                .media(scrap.getPublisherName())
//                .reporter(scrap.getReporterName())
//                .build();
//    }

