package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientGetResponse {
    private String name;

    private String logoUrl;

    @Builder
    public ClientGetResponse(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }
}
