package the_monitor.application.service;

import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.common.PageResponse;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Article;

public interface ArticleService {

    String saveArticles(Long clientId);

    PageResponse<ArticleResponse> getArticlesByClientAndCategoryType(CategoryType categoryType, int page);

    PageResponse<ArticleResponse> getArticlesByKeyword(CategoryType categoryType, Long keywordId, int page);

    Article findArticleById(Long articleId);

    String readArticle(Long articleId);

}
