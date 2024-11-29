package the_monitor.presentation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.EmailSendRequest;
import the_monitor.application.dto.request.EmailUpdateRequest;
import the_monitor.application.dto.response.EmailResponse;
import the_monitor.application.dto.response.EmailSendResponse;
import the_monitor.application.service.EmailService;
import the_monitor.common.ApiResponse;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 조회", description = "clientId에 따른 이메일 리스트를 조회합니다.")
    @GetMapping
    public ApiResponse<EmailResponse> getEmails() {

        return ApiResponse.onSuccessData("이메일 조회 성공", emailService.getEmails());

    }

    @Operation(summary = "이메일 수정", description = "clientId에 따른 이메일 리스트를 수정합니다.")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<EmailResponse> updateEmails(@RequestPart(name = "emailUpdate") @Valid EmailUpdateRequest emailUpdateRequest,
                                                   @RequestPart(value = "signatureImage", required = false) MultipartFile signatureImage) {

        return ApiResponse.onSuccessData("이메일 수정 성공", emailService.updateEmails(emailUpdateRequest, signatureImage));

    }

    @Operation(summary = "이메일 전송", description = "완성된 보고서를 고객사에게 전송합니다.")
    @PostMapping(value = "/send")
    public ApiResponse<EmailSendResponse> sendReport(@RequestParam("reportId") Long reportId,
                                                     @RequestBody @Valid EmailSendRequest emailSendRequest) throws MessagingException, UnsupportedEncodingException {

        return ApiResponse.onSuccessData("이메일 전송 성공", emailService.sendReportEmailWithAttachment(reportId,emailSendRequest.getSubject(), emailSendRequest.getContent()));

    }

}