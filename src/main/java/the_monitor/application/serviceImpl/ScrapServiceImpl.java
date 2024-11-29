package the_monitor.application.serviceImpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.response.ScrapCategoryTypeResponse;
import the_monitor.application.service.ClientService;
import the_monitor.application.service.ScrapService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.AccountRepository;
import the_monitor.domain.repository.ArticleRepository;
import the_monitor.domain.repository.ClientRepository;
import the_monitor.domain.repository.ScrapRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.ArrayList;
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

    // 스크랩하기
    @Override
    @Transactional
    public String scrapArticle(Long articleId) {

        // 1. Client 인증 정보 가져오기
        Long clientId = getClientIdFromAuthentication();

        // 2. Client 조회
        Client client = findClientById(clientId);

        // 3. Article 조회
        Article article = findArticleById(articleId);

        String responseMsg;

        if (article.isScrapped()) {
            findAndDeleteByClientAndTitleAndUrl(client, article);
            article.setScrapStatus(false);
            responseMsg = "스크랩 취소 완료";
        } else {
            buildAndSaveScrap(article, client);
            article.setScrapStatus(true);
            responseMsg = "스크랩 완료";
        }

        // 5. 저장된 Scrap ID 반환
        return responseMsg;

    }

    // 스크랩한 기사 조회
    @Override
    public ScrapCategoryTypeResponse getScrappedArticlesByClientId() {

        Long clientId = getClientIdFromAuthentication();

        List<Scrap> scrappedArticles = findAllByClientId(clientId);

        // Article 데이터를 ScrapArticleDto로 변환하여 CategoryType별로 그룹화
        Map<CategoryType, List<ScrapArticleDto>> groupedByCategory = groupedByCategory(scrappedArticles);

        // CategoryType별로 ScrapArticleDto를 담은 ScrapCategoryTypeResponse 생성
        return buildScrapCategoryTypeResponse(groupedByCategory);

    }

    // 스크랩 상세보기
    @Override
    public ScrapArticleDto getScrapArticleInfo(Long scrapId) {

        Scrap scrap = findScrapById(scrapId);

        return buildScrapArticleDto(scrap);

    }

    // 스크랩 취소
    @Override
    @Transactional
    public String unScrapArticle() {

        Long clientId = getClientIdFromAuthentication();

        List<Scrap> scraps = findAllByClientId(clientId);

        List<Long> originalArticleIds = scraps.stream()
                .map(Scrap::getOriginalArticleId)
                .toList();

        scrapRepository.deleteAll(scraps);

        for (Long originalArticleId : originalArticleIds) {
            Article article = findArticleById(originalArticleId);
            article.setScrapStatus(false);
        }

        return "스크랩 취소 완료";

    }

    // ClientId의 스크랩한 기사 조회
    @Override
    public List<Scrap> findAllByClientId(Long clientId) {
        return scrapRepository.findAllByClientId(clientId);
    }

    // 스크랩한 기사 삭제
    @Override
    @Transactional
    public void deleteScraps(List<Long> scrapIds) {
        scrapRepository.deleteAllById(scrapIds);
    }

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

    private Client findClientById(Long clientId) {
        return clientService.findClientById(clientId);
    }

    private Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ARTICLE_NOT_FOUND));
    }

    private Scrap findScrapById(Long scrapId) {
        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new ApiException(ErrorStatus._SCRAP_NOT_FOUND));
    }

    private void findAndDeleteByClientAndTitleAndUrl(Client client, Article article) {

        Scrap scrap = scrapRepository.findByClientAndTitleAndUrl(client, article.getTitle(), article.getUrl())
                .orElseThrow(() -> new ApiException(ErrorStatus._SCRAP_NOT_FOUND));

        scrapRepository.delete(scrap);

    }

    private void buildAndSaveScrap(Article article, Client client) {

        Scrap scrap = Scrap.builder()
                .originalArticleId(article.getId())
                .client(client)
                .title(article.getTitle())
                .url(article.getUrl())
                .keyword(article.getKeyword() != null ? article.getKeyword().getKeyword() : null)
                .publisherName(article.getPublisherName())
                .reporterName(article.getReporterName())
                .publishDate(article.getPublishDate())
                .categoryType(article.getKeyword() != null ? article.getKeyword().getCategory().getCategoryType() : null)
                .build();

        scrapRepository.save(scrap);

    }

    private Map<CategoryType, List<ScrapArticleDto>> groupedByCategory(List<Scrap> scrappedArticles) {

        return scrappedArticles.stream()
                .map(scrap -> ScrapArticleDto.builder()
                        .originalArticleId(scrap.getOriginalArticleId())
                        .scrapId(scrap.getId())
                        .keyword(scrap.getKeyword())
                        .title(scrap.getTitle())
                        .url(scrap.getUrl())
                        .publisherName(scrap.getPublisherName())
                        .publishDate(scrap.getPublishDate())
                        .reporterName(scrap.getReporterName())
                        .categoryType(scrap.getCategoryType())
                        .build())
                .filter(dto -> dto.getCategoryType() != null) // categoryType이 null이 아닌 경우만 포함
                .collect(Collectors.groupingBy(ScrapArticleDto::getCategoryType));

    }

    private ScrapCategoryTypeResponse buildScrapCategoryTypeResponse(Map<CategoryType, List<ScrapArticleDto>> groupedByCategory) {

        List<ScrapArticleDto> selfArticles = groupedByCategory.getOrDefault(CategoryType.SELF, new ArrayList<>());
        List<ScrapArticleDto> competitorArticles = groupedByCategory.getOrDefault(CategoryType.COMPETITOR, new ArrayList<>());
        List<ScrapArticleDto> industryArticles = groupedByCategory.getOrDefault(CategoryType.INDUSTRY, new ArrayList<>());

        return ScrapCategoryTypeResponse.builder()
                .scrapSelfResponses(selfArticles)
                .scrapCompetitorResponses(competitorArticles)
                .scrapIndustryResponses(industryArticles)
                .build();

    }

    private ScrapArticleDto buildScrapArticleDto(Scrap scrap) {

        return ScrapArticleDto.builder()
                .originalArticleId(scrap.getOriginalArticleId())
                .scrapId(scrap.getId())
                .keyword(scrap.getKeyword())
                .title(scrap.getTitle())
                .url(scrap.getUrl())
                .publisherName(scrap.getPublisherName())
                .publishDate(scrap.getPublishDate())
                .reporterName(scrap.getReporterName())
                .categoryType(scrap.getCategoryType())
                .build();

    }

}


