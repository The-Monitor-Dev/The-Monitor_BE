package the_monitor.application.service;


import jakarta.mail.MessagingException;
import the_monitor.application.dto.request.EmailUpdateRequest;
import the_monitor.application.dto.response.EmailResponse;
import the_monitor.domain.model.Client;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface EmailService {

    void sendEmail(String to, String subject, String text) throws MessagingException, UnsupportedEncodingException;

    void saveEmails(List<String> recipientEmails, List<String> ccEmails, Client client);

    EmailResponse getEmails(Long clientId);

    EmailResponse updateEmails(Long clientId, EmailUpdateRequest emailUpdateRequest);}
