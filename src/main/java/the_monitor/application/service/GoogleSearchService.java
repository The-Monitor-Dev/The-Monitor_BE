package the_monitor.application.service;

import the_monitor.application.dto.response.ArticleResponse;

public interface GoogleSearchService {

    ArticleResponse toDto(String keyword);

}
