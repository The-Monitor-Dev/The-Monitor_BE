package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

import javax.imageio.metadata.IIOInvalidTreeException;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class KeywordResponse {

    @JsonProperty("SELF")
    private List<KeywordAndIdResponse> self;
    @JsonProperty("COMPETITOR")
    private List<KeywordAndIdResponse> competitor;
    @JsonProperty("INDUSTRY")
    private List<KeywordAndIdResponse> industry;

    @Builder
    public KeywordResponse(List<KeywordAndIdResponse> self,
                           List<KeywordAndIdResponse> competitor,
                           List<KeywordAndIdResponse> industry) {

        this.self = self;
        this.competitor = competitor;
        this.industry = industry;

    }

}
