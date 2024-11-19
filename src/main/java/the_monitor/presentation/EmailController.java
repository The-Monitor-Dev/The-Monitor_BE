package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.dto.request.EmailUpdateRequest;
import the_monitor.application.dto.response.EmailResponse;
import the_monitor.application.service.EmailService;
import the_monitor.common.ApiResponse;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @Operation(summary = "이메일 조회", description = "clientId에 따른 이메일 리스트를 조회합니다.")
    @GetMapping
    public ApiResponse<EmailResponse> getEmails(@RequestParam("clientId") Long clientId) {

        return ApiResponse.onSuccessData("이메일 조회 성공", emailService.getEmails(clientId));
    }

    @Operation(summary = "이메일 수정", description = "clientId에 따른 이메일 리스트를 수정합니다.")
    @PutMapping
    public ApiResponse<EmailResponse> updateEmails(@RequestParam("clientId") Long clientId, @RequestBody EmailUpdateRequest emailUpdateRequest) {

        return ApiResponse.onSuccessData("이메일 수정 성공", emailService.updateEmails(clientId, emailUpdateRequest));
    }

}