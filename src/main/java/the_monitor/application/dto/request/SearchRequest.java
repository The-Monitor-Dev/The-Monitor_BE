package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchRequest {
    private String searchText;

    @Builder
    public SearchRequest(String searchText) {
        this.searchText = searchText;
    }
}
