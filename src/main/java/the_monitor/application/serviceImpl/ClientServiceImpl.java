package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import the_monitor.application.dto.request.ClientRequest;
import the_monitor.application.dto.request.ClientUpdateRequest;
import the_monitor.application.dto.response.ClientGetResponse;
import the_monitor.application.dto.response.ClientResponse;
import the_monitor.application.service.*;
import org.springframework.stereotype.Service;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.*;
import the_monitor.infrastructure.jwt.JwtProvider;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CategoryService categoryService;
    private final AccountRepository accountRepository;

    private final S3Service s3Service;
    private final ArticleService articleService;
    private final AccountService accountService;
    private final EmailService emailService;

    private final JwtProvider jwtProvider;

    @Value("${cloud.aws.s3.default-logo-url}")
    private String defaultLogoUrl;

    // 클라이언트 생성
    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest clientRequest, MultipartFile logo) {

        // JWT에서 accountId를 추출하는 과정
        Long extractedAccountId = getAccountIdFromJwt();
        Account account = accountRepository.findById(extractedAccountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));

        String logoPath;
        logoPath = (logo != null) ? s3Service.uploadFile(logo) : defaultLogoUrl;

        // 클라이언트 객체 생성
        Client client = Client.builder()
                .name(clientRequest.getName())
                .managerName(clientRequest.getManagerName())
                .logo(logoPath)
                .account(account)
                .build();

        client = clientRepository.save(client);

        // 카테고리 및 키워드 저장
        Map<CategoryType, List<String>> categoryKeywords = clientRequest.getCategoryKeywords();
        for (Map.Entry<CategoryType, List<String>> entry : categoryKeywords.entrySet()) {
            categoryService.saveCategoryWithKeywords(entry.getKey(), entry.getValue(), client); // CategoryService 사용
        }

        // 이메일 수신자와 참조인 저장
        emailService.saveEmails(clientRequest.getRecipientEmails(), clientRequest.getCcEmails(), client);

        // 기사 저장
        articleService.saveArticles(client.getId());

        // ClientResponse 반환
        return ClientResponse.builder()
                .clientId(client.getId())
                .name(client.getName())
                .managerName(client.getManagerName())
                .logoUrl(client.getLogo())
                .build();

    }

    @Override
    public List<ClientResponse> getClientsByAccountId() {
        Long accountId = getAccountIdFromJwt(); // JWT에서 accountId 추출
        List<Client> clients = clientRepository.findAllByAccountId(accountId);
        if (clients.isEmpty()) {
            return List.of();
        }

        return clients.stream()
                .map(client -> ClientResponse.builder()
                        .clientId(client.getId())
                        .name(client.getName())
                        .managerName(client.getManagerName())
                        .logoUrl(client.getLogo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Client findClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));
    }

    @Override
    public ClientGetResponse getClient(){

        Long accountId = getAccountId();
        Long clientId = getClientIdFromAuthentication();


        // clientId와 accountId를 동시에 검증
        Client client = clientRepository.findByIdAndAccountId(clientId, accountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_FORBIDDEN));

        // Client 객체를 ClientResponse로 변환
        return ClientGetResponse.builder()
                .name(client.getName())
                .logoUrl(client.getLogo())
                .build();
    }

    private Long getAccountIdFromJwt() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return jwtProvider.getAccountId(token);
    }

    @Override
    @Transactional
    public String deleteClientById(Long clientId) {
        Long accountId = getAccountIdFromJwt(); // JWT에서 accountId 추출 (유틸리티 메서드)

        // 1. Client 존재 여부 및 권한 확인
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_NOT_FOUND));

        if (!client.getAccount().getId().equals(accountId)) {
            throw new ApiException(ErrorStatus._CLIENT_FORBIDDEN);
        }

        // 2. Client 삭제
        clientRepository.delete(client);

        // 3. 성공 메시지 반환
        return "고객사 정보가 성공적으로 삭제되었습니다.";
    }

    @Override
    @Transactional
    public String updateClient(Long clientId, ClientUpdateRequest request, MultipartFile logo) {
        Long accountId = getAccountIdFromJwt();

        // clientId와 accountId를 동시에 검증
        Client client = clientRepository.findByIdAndAccountId(clientId, accountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_FORBIDDEN));

        String logoPath;
        logoPath = (logo != null) ? s3Service.uploadFile(logo) : defaultLogoUrl;

        // 2. 수정 사항 적용
        client.updateClientInfo(request.getName(), request.getManagerName(), logoPath);

        return "고객사 정보가 성공적으로 수정되었습니다.";
    }

    @Override
    public List<ClientResponse> searchClient(String searchText) {
        // JWT에서 accountId 추출
        Long extractedAccountId = getAccountIdFromJwt();

        // 계정 검증
        Account account = accountRepository.findById(extractedAccountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._ACCOUNT_NOT_FOUND));

        // 검색 텍스트가 없으면 빈 리스트 반환
        if (searchText == null || searchText.trim().isEmpty()) {
            return List.of();
        }

        // 해당 계정의 클라이언트 중 검색
        List<Client> clients = clientRepository.findByAccountAndNameContainingIgnoreCase(account, searchText);

        // 결과 변환 및 반환
        return clients.stream()
                .map(client -> ClientResponse.builder()
                        .clientId(client.getId())
                        .name(client.getName())
                        .managerName(client.getManagerName())
                        .logoUrl(client.getLogo())
                        .build())
                .collect(Collectors.toList());
    }

    private Long getAccountId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();

    }

    private Account findAccountById() {
        return accountService.findAccountById(getAccountId());
    }

    private Long getClientIdFromAuthentication() {
        Account account = findAccountById();
        return account.getSelectedClientId();
    }

}