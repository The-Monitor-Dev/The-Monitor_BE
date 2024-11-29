package the_monitor.application.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportUpdateColorRequest {

    private String color;

    @Builder
    public ReportUpdateColorRequest(String color) {
        this.color = color;
    }

}
