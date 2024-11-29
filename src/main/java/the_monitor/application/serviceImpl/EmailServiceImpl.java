package the_monitor.application.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.application.dto.request.EmailUpdateRequest;
import the_monitor.application.dto.response.EmailResponse;
import the_monitor.application.dto.response.EmailSendResponse;
import the_monitor.application.service.EmailService;
import the_monitor.application.service.ExcelService;
import the_monitor.application.service.S3Service;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.ClientMailCC;
import the_monitor.domain.model.ClientMailRecipient;
import the_monitor.domain.repository.*;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final ClientMailRecipientRepository clientMailRecipientRepository;
    private final ClientMailCCRepository clientMailCCRepository;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    private final ExcelService excelService;
    private final S3Service s3Service;


    @Override
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom("themonitor2024@gmail.com", "The Monitor"); //보내는사람
        helper.setTo(toEmail); //받는사람
        helper.setSubject(subject); //메일제목
        helper.setText(body, true); //ture넣을경우 html

        javaMailSender.send(mimeMessage);

    }

    @Override
    public void saveEmails(List<String> recipientEmails, List<String> ccEmails, Client client) {

        // 수신자 이메일 저장
        List<ClientMailRecipient> recipients = new ArrayList<>();
        for (String email : recipientEmails) {
            ClientMailRecipient recipient = ClientMailRecipient.builder()
                    .address(email)
                    .client(client)
                    .build();
            recipients.add(recipient);
        }
        clientMailRecipientRepository.saveAll(recipients);
        client.setClientMailRecipients(recipients);

        // 참조자 이메일 저장
        List<ClientMailCC> ccs = new ArrayList<>();
        for (String email : ccEmails) {
            ClientMailCC cc = ClientMailCC.builder()
                    .address(email)
                    .client(client)
                    .build();
            ccs.add(cc);
        }
        clientMailCCRepository.saveAll(ccs);
        client.setClientMailCCs(ccs);

    }

    @Override
    public EmailResponse getEmails() {

        Long accountId = getAccountId();
        Long clientId = getClientIdFromAuthentication();

        Client client = clientRepository.findByIdAndAccountId(clientId, accountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_FORBIDDEN));

        List<String> recipients = clientMailRecipientRepository.findAllByClient(client)
                .stream()
                .map(ClientMailRecipient::getAddress)
                .collect(Collectors.toList());

        List<String> ccs = clientMailCCRepository.findAllByClient(client)
                .stream()
                .map(ClientMailCC::getAddress)
                .collect(Collectors.toList());

        String signatureImageUrl = null;
        if (client.getSignatureUrl() != null) {
            signatureImageUrl = client.getSignatureUrl();
        }

        return EmailResponse.builder()
                .recipients(recipients)
                .ccs(ccs)
                .signatureImageUrl(signatureImageUrl)
                .build();
    }

    @Override
    @Transactional
    public EmailResponse updateEmails(EmailUpdateRequest emailUpdateRequest, MultipartFile signatureImage) {

        Long accountId = getAccountId();
        Long clientId = getClientIdFromAuthentication();

        Client client = clientRepository.findByIdAndAccountId(clientId, accountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_FORBIDDEN));

        clientMailRecipientRepository.deleteAllByClient(client);
        clientMailCCRepository.deleteAllByClient(client);

        saveEmails(emailUpdateRequest.getRecipients(), emailUpdateRequest.getCcs(), client);

        String signatureImageUrl = null;

// 서명 이미지 처리
        if (signatureImage != null && !signatureImage.isEmpty()) {
            // S3에 이미지 업로드 후 URL 가져오기
            String imageUrl = s3Service.uploadFile(signatureImage);

            // Client의 signatureUrl 필드 업데이트
            client.updateImageUrl(imageUrl);

            // 변경된 Client 엔티티 저장
            clientRepository.save(client);

            // 반환할 서명 이미지 URL 설정
            signatureImageUrl = imageUrl;
        } else if (client.getSignatureUrl() != null) {
            // 기존 서명 이미지 URL 반환
            signatureImageUrl = client.getSignatureUrl();
        }

        List<String> recipientEmails = clientMailRecipientRepository.findAllByClient(client)
                .stream()
                .map(ClientMailRecipient::getAddress)
                .collect(Collectors.toList());

        List<String> ccEmails = clientMailCCRepository.findAllByClient(client)
                .stream()
                .map(ClientMailCC::getAddress)
                .collect(Collectors.toList());


        return EmailResponse.builder()
                .recipients(recipientEmails)
                .ccs(ccEmails)
                .signatureImageUrl(signatureImageUrl)
                .build();
    }

    @Override
    public EmailSendResponse sendReportEmailWithAttachment(Long reportId, String subject, String content) {

        File excelFile = null;
        try {
            Long clientId = getClientIdFromAuthentication();

            // 1. Client 조회
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

            // 2. 수신자 이메일 조회
            List<String> toEmails = clientMailRecipientRepository.findAllByClient(client).stream()
                    .map(ClientMailRecipient::getAddress)
                    .toList();

            // 3. 참조 이메일 조회
            List<String> ccEmails = clientMailCCRepository.findAllByClient(client).stream()
                    .map(ClientMailCC::getAddress)
                    .toList();

            // 4. 엑셀 파일 생성
            excelFile = excelService.createExcelFile(reportId);

            // 5. 이메일 전송 준비
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom("themonitor2024@gmail.com", "The Monitor");

            helper.setTo(toEmails.toArray(new String[0]));
            if (!ccEmails.isEmpty()) {
                helper.setCc(ccEmails.toArray(new String[0]));
            }
            helper.setSubject(subject);
            helper.setText(content, true);

            // 6. 첨부 파일 추가
            if (excelFile != null) {
                helper.addAttachment("report.xlsx", excelFile);
            }

            // 7. 이메일 전송
            javaMailSender.send(mimeMessage);

            // 8. 응답 빌드
            return EmailSendResponse.builder()
                    .toEmails(toEmails)
                    .ccEmails(ccEmails)
                    .build();

        } catch (MessagingException e) {
            throw new ApiException(ErrorStatus._EMAIL_SEND_FAIL);
        } catch (IOException e) {
            throw new ApiException(ErrorStatus._FILE_OPERATION_FAIL);
        } finally {
            // 9. 임시 파일 삭제
            if (excelFile != null && excelFile.exists()) {
                excelFile.delete();
            }
        }

    }

    private Long getAccountId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();

    }

    private Account findAccountById() {
        return accountRepository.findAccountById(getAccountId());
    }

    private Long getClientIdFromAuthentication() {
        Account account = findAccountById();
        return account.getSelectedClientId();
    }

}