package the_monitor.application.service;

import the_monitor.application.dto.request.ScrapIdsRequest;
import the_monitor.application.dto.request.ScrapReportArticleRequest;
import the_monitor.application.dto.response.ScrapArticleListResponse;
import the_monitor.application.dto.response.ScrapReportArticeResponse;
import the_monitor.domain.model.Scrap;

public interface ScrapService {

    Scrap findById(Long scrapId);

    ScrapReportArticeResponse scrapArticle(Long clientId, ScrapReportArticleRequest request);

    ScrapArticleListResponse getScrapArticleList(Long clientId, ScrapIdsRequest request);

}
