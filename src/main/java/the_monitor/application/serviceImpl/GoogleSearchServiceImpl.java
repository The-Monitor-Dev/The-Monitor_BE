package the_monitor.application.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.service.GoogleSearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSearchServiceImpl implements GoogleSearchService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.api.search-engine-id}")
    private String searchEngineId;

    @Value("${google.api.base-url:https://www.googleapis.com/customsearch/v1}")
    private String baseUrl;

    @Override
    public ArticleResponse toDto(String keyword) {

        List<ArticleGoogleDto> allResults = new ArrayList<>();
        int start = 1;
        int totalResults = 0;

        while (start <= 100) {
            ArticleResponse pageResults = searchArticles(keyword, start);

            allResults.addAll(pageResults.getGoogleArticles());
            totalResults = pageResults.getTotalResults(); // 전체 결과 수 업데이트
            start += 10;

        }

        return ArticleResponse.builder()
                .googleArticles(allResults)
                .totalResults(totalResults)
                .build();

    }

    public ArticleResponse searchArticles(String query, int start) {

        int retryCount = 3; // 최대 재시도 횟수
        int attempt = 0;
        while (true) {
            try {
                String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .queryParam("q", query)
                        .queryParam("key", apiKey)
                        .queryParam("cx", searchEngineId)
                        .queryParam("num", 10)
                        .queryParam("start", start)
                        .build(false)
                        .toUriString();

                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                System.out.println("검색어 : " + query + " / 페이지 : " + start);

                if (response.getStatusCode() == HttpStatus.OK) {
                    return parseResponse(response.getBody());
                } else {
                    throw new RuntimeException("Failed to search Google: " + response.getStatusCode());
                }
            } catch (Exception e) {
                attempt++;
                if (attempt >= retryCount) {
                    throw new RuntimeException("Failed to search Google after retries: " + e.getMessage(), e);
                }
                System.out.println("Retrying... Attempt " + attempt);
            }
        }

    }

    private ArticleResponse parseResponse(String jsonResponse) {

        List<ArticleGoogleDto> searchDetails = new ArrayList<>();

        int totalResults = 0;

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            // 총 검색 결과 수 가져오기
            totalResults = root.path("searchInformation").path("totalResults").asInt(0);

            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                String title = item.path("title").asText();
                String snippet = item.path("snippet").asText();
                String link = item.path("link").asText();

                // 이미지 URL 가져오기 (cse_image 노드가 있을 경우)
                String imageUrl = "";
                JsonNode cseImageNode = item.path("pagemap").path("cse_image");
                if (cseImageNode.isArray() && cseImageNode.has(0)) {
                    imageUrl = cseImageNode.get(0).path("src").asText();
                }

                // 출판사와 날짜 정보 가져오기 (metatags 노드가 있을 경우)
                String publisher = "";
                String publishDate = "";
                String reporter = "";
                JsonNode metatagsNode = item.path("pagemap").path("metatags");
                if (metatagsNode.isArray() && metatagsNode.has(0)) {
                    publisher = metatagsNode.get(0).path("og:site_name").asText("");
                    publishDate = metatagsNode.get(0).path("article:published_time").asText("");
                    reporter = metatagsNode.get(0).path("dable:author").asText("");
                }

                // publisher와 publishDate가 null이면 추가하지 않음.
                if ((publisher == null || publisher.isEmpty()) && publishDate.isEmpty()) {
                    continue;
                }

                ArticleGoogleDto dto = ArticleGoogleDto.builder()
                        .title(title)
                        .body(snippet)
                        .url(link)
                        .imageUrl(imageUrl)
                        .publisherName(publisher)
                        .publishDate(publishDate)
                        .reporterName(reporter)
                        .build();

                searchDetails.add(dto);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ArticleResponse.builder()
                .googleArticles(searchDetails)
                .totalResults(totalResults)
                .build();

    }

}