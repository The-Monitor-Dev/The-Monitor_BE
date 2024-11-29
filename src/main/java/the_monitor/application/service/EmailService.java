package the_monitor.application.service;


import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.EmailUpdateRequest;
import the_monitor.application.dto.response.EmailResponse;
import the_monitor.application.dto.response.EmailSendResponse;
import the_monitor.domain.model.Client;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface EmailService {

    void sendEmail(String to, String subject, String text) throws MessagingException, UnsupportedEncodingException;

    void saveEmails(List<String> recipientEmails, List<String> ccEmails, Client client);

    EmailResponse getEmails();

    EmailResponse updateEmails(EmailUpdateRequest emailUpdateRequest, MultipartFile signatureImage);

    EmailSendResponse sendReportEmailWithAttachment(Long reportId, String subject, String content) throws MessagingException, UnsupportedEncodingException;
}