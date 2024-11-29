package the_monitor.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ReasonDto {

    private HttpStatus httpStatus;
    private boolean isSuccess;
    private String code;
    private String message;

    @Builder
    public ReasonDto(
            HttpStatus httpStatus,
            boolean isSuccess,
            String code,
            String message) {

        this.httpStatus = httpStatus;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;

    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

}