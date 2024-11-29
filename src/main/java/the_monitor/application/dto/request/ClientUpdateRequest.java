package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientUpdateRequest {

    @NotBlank(message = "고객사명은 필수입니다.")
    private String name;

    @NotBlank(message = "담당자명은 필수입니다.")
    private String managerName;
}