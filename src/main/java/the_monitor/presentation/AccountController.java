package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.*;
import the_monitor.application.service.AccountService;
import the_monitor.common.ApiResponse;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "이메일 인증 요청", description = "이메일 인증 요청을 보냅니다.")
    @PostMapping("/sendEmailConfirm")
    public ApiResponse<String> sendEmailConfirm(@RequestBody @Valid AccountEmailRequest request) {

        return ApiResponse.onSuccess(accountService.sendEmailConfirm(request));

    }

    @Operation(summary = "인증코드 확인", description = "인증코드를 확인합니다.")
    @PostMapping("/verifyCode")
    public ApiResponse<String> verifyCode(@RequestBody @Valid AccountEmailCertifyRequest request) {

        return ApiResponse.onSuccess(accountService.verifyCode(request));

    }

    @Operation(summary = "계정 생성", description = "회원가입을 진행합니다.")
    @PostMapping("/signUp")
    public ApiResponse<String> createAccount(@RequestBody @Valid AccountSignUpRequest request){

        return ApiResponse.onSuccess(accountService.accountSignUp(request));

    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/signIn")
    public ApiResponse<String> Login(@RequestBody @Valid AccountSignInRequest request, HttpServletResponse response, HttpSession session) {

        return accountService.accountSignIn(request, response, session);
    }

    @Operation(summary = "토큰 유효성 검사", description = "토큰 유효성 검사")
    @GetMapping("/tokenValidity")
    public ApiResponse<String> checkTokenValidity(HttpServletRequest request, HttpServletResponse response) {

        return accountService.checkTokenValidity(request,response);

    }

    @Operation(summary = "비밀번호 변경 메일 발송", description = "비밀번호 변경 메일을 발송합니다.")
    @PostMapping("/sendPasswordChangeEmail")
    public ApiResponse<String> sendPasswordChangeEmail(@RequestBody @Valid AccountEmailRequest request) throws UnsupportedEncodingException {

        return ApiResponse.onSuccess(accountService.sendPasswordChangeEmail(request));

    }

    @Operation(summary = "client id 설정", description = "client id 설정")
    @PostMapping("set-client")
    public ApiResponse<String> setClient(@RequestParam("clientId") Long clientId) {

        return ApiResponse.onSuccess(accountService.setClientId(clientId));

    }

}
