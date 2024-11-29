package the_monitor.application.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class EmailUpdateRequest {

    private List<String> recipients;
    private List<String> ccs;
}