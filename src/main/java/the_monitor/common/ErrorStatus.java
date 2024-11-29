package the_monitor.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import the_monitor.application.dto.ErrorReasonDto;
import the_monitor.domain.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 응답
    _ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT404", "해당 계정을 찾을 수 없습니다."),
    _ACCOUNT_NOT_EXIST(HttpStatus.NOT_FOUND, "ACCOUNT404", "가입되지 않은 이메일 주소입니다."),
    _ACCOUNT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "ACCOUNT400", "이미 존재하는 계정입니다."),
    _WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT400", "비밀번호가 일치하지 않습니다."),
    _SAME_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT400", "기존 비밀번호와 동일합니다."),
    _CERTIFIED_KEY_EXPIRED(HttpStatus.BAD_REQUEST, "ACCOUNT400", "입력 가능한 시간이 초과되었습니다."),
    _INVALID_CERTIFIED_KEY(HttpStatus.BAD_REQUEST, "ACCOUNT400", "인증 번호가 일치하지 않습니다."),
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증되지 않은 요청입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    _EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "EMAIL400", "이메일 전송에 실패했습니다."),

    // FILE
    _FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE404", "해당 파일을 찾을 수 없습니다."),
    _FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE400", "파일 업로드에 실패했습니다."),
    _FILE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "FILE400", "파일 삭제에 실패했습니다."),
    _FILE_DOWNLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE400", "파일 다운로드에 실패했습니다."),
    _FILE_OPERATION_FAIL(HttpStatus.BAD_REQUEST, "FILE400","파일 생성에 실패했습니다."),

    // JWT
    _JWT_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT404", "토큰을 찾을 수 없습니다"),
    _JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT400", "유효하지 않는 토큰입니다"),
    _JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT400", "만료된 토큰입니다"),
    _JWT_BLACKLIST(HttpStatus.UNAUTHORIZED, "JWT400", "접근 불가능한 토큰입니다"),
    _JWT_UNKNOWN(HttpStatus.UNAUTHORIZED, "JWT400", "JWT 인증 중 알 수 없는 오류가 발생했습니다."),

    _UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "알 수 없는 오류가 발생했습니다."),

    // Article
    _ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE404", "해당 기사를 찾을 수 없습니다."),

    // Client
    _CLIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CLIENT404", "해당 클라이언트를 찾을 수 없습니다."),
    _CLIENT_FORBIDDEN(HttpStatus.FORBIDDEN, "CLIENT403", "해당 클라이언트에 접속할 수 없습니다,"),

    // Report
    _REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404", "해당 리포트를 찾을 수 없습니다."),
    _REPORT_FORBIDDEN(HttpStatus.FORBIDDEN, "REPORT403", "해당 리포트에 접근할 수 없습니다."),
    _REPORT_ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404", "해당 리포트 기사를 찾을 수 없습니다."),
    _INVALID_REPORT_ARTICLE_SUMMARY_LENGTH(HttpStatus.BAD_REQUEST, "REPORT400", "리포트 기사 요약은 100자 이하로 입력해주세요."),

    // ReportCategory
    _REPORT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404", "해당 리포트 카테고리를 찾을 수 없습니다."),
    // SCRAP
    _SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCRAP404", "해당 스크랩을 찾을 수 없습니다."),
    //File
    _FILE_RETRIEVE_FAILED(HttpStatus.NOT_FOUND,"FILE404", "S3에서 최신 파일 조회 중 오류 발생");



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