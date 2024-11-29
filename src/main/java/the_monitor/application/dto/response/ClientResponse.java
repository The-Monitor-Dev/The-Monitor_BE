package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.enums.CategoryType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ClientResponse {

    private Long clientId;
    private String name;
    private String managerName;
    private String logoUrl;


    @Builder
    public ClientResponse(Long clientId,
                          String name,
                          String managerName,
                          String logoUrl) {

        this.clientId = clientId;
        this.name = name;
        this.managerName = managerName;
        this.logoUrl = logoUrl;
    }
}