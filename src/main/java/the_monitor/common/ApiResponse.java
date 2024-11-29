package the_monitor.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import the_monitor.domain.BaseCode;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    @Builder
    public ApiResponse(
            boolean isSuccess,
            String code,
            String message,
            T result ) {

        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;

    }

    // 성공한 경우 응답 생성
//    public static <T> ApiResponse<T> onSuccess(T result) {
//
//        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(),
//                result);
//
//    }

    public static <T> ApiResponse<T> of(BaseCode code, T result) {

        return new ApiResponse<>(true, code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(), result);

    }

    public static <T> ApiResponse<T> onCustomSuccessData(String code, String message, T result) {
        return new ApiResponse<>(true, code, message, result);
    }

    public static <T> ApiResponse<T> onSuccess(String message) {

        return new ApiResponse<>(true, SuccessStatus._OK.getCode(),
                message, null);

    }

    public static <T> ApiResponse<T> onSuccessData(String message, T result) {

        return new ApiResponse<>(true, SuccessStatus._OK.getCode(),
                message, result);

    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {

        return new ApiResponse<>(false, code, message, data);

    }



}