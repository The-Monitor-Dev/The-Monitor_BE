package the_monitor.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import the_monitor.application.ErrorReasonDto;
import the_monitor.domain.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증되지 않은 요청입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    _FILE_DOWNLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE404", "파일을 다운받을 수 없습니다."),
    _ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN404", "해당 관리자를 찾을 수 없습니다."),
    _ADMIN_NOT_VALID(HttpStatus.UNAUTHORIZED, "ADMIN400", "비밀번호가 일치하지 않습니다"),
    _JWT_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT404", "토큰을 찾을 수 없습니다"),
    _JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT400", "유효하지 않는 토큰입니다"),
    _JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT400", "만료된 토큰입니다"),
    _JWT_BLACKLIST(HttpStatus.UNAUTHORIZED, "JWT400", "접근 불가능한 토큰입니다");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

}