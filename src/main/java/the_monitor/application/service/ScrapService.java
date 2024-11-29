package the_monitor.application.service;

import the_monitor.application.dto.ScrapArticleDto;
import the_monitor.application.dto.response.ScrapCategoryTypeResponse;
import the_monitor.domain.model.Scrap;

import java.util.List;

public interface ScrapService {

    String scrapArticle(Long articleId);

    ScrapCategoryTypeResponse getScrappedArticlesByClientId();

    ScrapArticleDto getScrapArticleInfo(Long scrapId);

    String unScrapArticle();

    List<Scrap> findAllByClientId(Long clientId);

    void deleteScraps(List<Long> scrapIds);

}
