package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.ArticleService;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.application.service.KeywordService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.common.PageResponse;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Account;
import the_monitor.domain.model.Article;
import the_monitor.domain.model.Category;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.ArticleRepository;
import the_monitor.domain.repository.ReportArticleRepository;
import the_monitor.infrastructure.jwt.JwtProvider;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final GoogleSearchService googleSearchService;
    private final ArticleRepository articleRepository;
    private final KeywordService keywordService;

    @Override
    public Article findArticleById(Long articleId) {

        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ARTICLE_NOT_FOUND));

    }

    @Override
    @Transactional
    public String saveArticles(Long clientId) {
        Long accountId = getAccountId();

        for (CategoryType categoryType : CategoryType.values()) {
            List<Keyword> keywords = keywordService.getKeywordByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

            for (Keyword keyword : keywords) {
                saveArticlesFromGoogle(keyword);
            }
        }

        return "기사 저장 완료";

    }

    private void saveArticlesFromGoogle(Keyword keyword) {

        ArticleResponse articleResponse = googleSearchService.toDto(keyword.getKeyword());

        for (ArticleGoogleDto dto : articleResponse.getGoogleArticles()) {
            articleRepository.save(dto.toEntity(keyword));
        }

    }

    @Override
    public PageResponse<ArticleResponse> getArticlesByClientAndCategoryType(Long clientId, CategoryType categoryType, int page) {

        // 페이지네이션 처리
        Pageable pageable = PageRequest.of(page - 1, 10); // 페이지는 0부터 시작, size는 10

        // Repository 메서드 호출
        Page<Article> articlePage = articleRepository.findByClientIdAndCategoryType(clientId, categoryType, pageable);

        // 조회된 기사들을 ArticleResponse로 변환
        List<ArticleGoogleDto> articleDtos = articlePage.getContent().stream()
                .map(article -> ArticleGoogleDto.builder()
                        .articleId(article.getId())
                        .title(article.getTitle())
                        .body(article.getBody())
                        .url(article.getUrl())
                        .imageUrl(article.getImageUrl())
                        .publisherName(article.getPublisherName())
                        .publishDate(article.getPublishDate())
                        .reporterName(article.getReporterName())
                        .build())
                .toList();

        ArticleResponse articleResponse = ArticleResponse.builder()
                .googleArticles(articleDtos)
                .totalResults((int) articlePage.getTotalElements())
                .build();

        return PageResponse.<ArticleResponse>builder()
                .listPageResponse(List.of(articleResponse))
                .totalCount(articlePage.getTotalElements())
                .size(articlePage.getSize())
                .build();
    }

    @Override
    public PageResponse<ArticleResponse> getArticlesByKeyword(Long clientId, CategoryType categoryType, Long keywordId, int page) {

        Long accountId = getAccountId();

        // 특정 Keyword 가져오기
        Keyword keyword = keywordService.getKeywordByIdAndAccountIdAndClientIdAndCategoryType(keywordId, accountId, clientId, categoryType);

        if (keyword == null) {
            throw new IllegalArgumentException("Keyword not found");
        }

        // 페이지네이션 처리
        Pageable pageable = PageRequest.of(page - 1, 10); // 페이지는 0부터 시작, size는 10

        // DB에서 특정 Keyword에 해당하는 Article 조회
        Page<Article> articlePage = articleRepository.findByKeyword(keyword, pageable);

        // 조회된 기사들을 ArticleResponse로 변환
        List<ArticleGoogleDto> articleDtos = articlePage.getContent().stream()
                .map(article -> ArticleGoogleDto.builder()
                        .title(article.getTitle())
                        .body(article.getBody())
                        .url(article.getUrl())
                        .imageUrl(article.getImageUrl())
                        .publisherName(article.getPublisherName())
                        .publishDate(article.getPublishDate())
                        .reporterName(article.getReporterName())
                        .build())
                .toList();

        ArticleResponse articleResponse = ArticleResponse.builder()
                .googleArticles(articleDtos)
                .totalResults((int) articlePage.getTotalElements())
                .build();

        return PageResponse.<ArticleResponse>builder()
                .listPageResponse(List.of(articleResponse))
                .totalCount(articlePage.getTotalElements())
                .size(articlePage.getSize())
                .build();
    }


    private Long getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();
    }

}