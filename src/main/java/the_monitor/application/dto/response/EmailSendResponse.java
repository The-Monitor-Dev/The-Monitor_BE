package the_monitor.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class EmailSendResponse {
    private List<String> toEmails;  // 수신자 이메일 리스트
    private List<String> ccEmails; // 참조자 이메일 리스트

    @Builder
    public EmailSendResponse(List<String> toEmails, List<String> ccEmails, String status) {
        this.toEmails = toEmails;
        this.ccEmails = ccEmails;
    }
}
