package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.dto.ArticleGoogleDto;
import the_monitor.application.dto.request.KeywordUpdateRequest;
import the_monitor.application.dto.response.ArticleResponse;
import the_monitor.application.dto.response.KeywordAndIdResponse;
import the_monitor.application.dto.response.KeywordResponse;
import the_monitor.application.service.AccountService;
import the_monitor.application.service.GoogleSearchService;
import the_monitor.application.service.KeywordService;
import the_monitor.common.ApiException;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Account;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.ArticleRepository;
import the_monitor.domain.repository.ClientRepository;
import the_monitor.domain.repository.KeywordRepository;
import the_monitor.infrastructure.security.CustomUserDetails;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final GoogleSearchService googleSearchService;
    private final KeywordRepository keywordRepository;
    private final ClientRepository clientRepository;
    private final CategoryServiceImpl categoryServiceImpl;
    private final AccountService accountService;
    private final ArticleRepository articleRepository;

    // 키워드 조회
    @Override
    public KeywordResponse getKeywords() {

        Long accountId = getAccountIdFromAuthentication();

        Long clientId = getClientIdFromAuthentication();

        return getKeywordResponses(accountId, clientId);

    }

    // AccountId, ClientId, CategoryType에 해당하는 키워드 조회
    @Override
    public List<Keyword> getKeywordByAccountIdAndClientIdAndCategoryType(Long accountId, Long clientId, CategoryType categoryType) {

        return keywordRepository.findKeywordsByAccountIdAndClientIdAndCategoryType(accountId, clientId, categoryType);

    }

    // 키워드 ID, CategoryType에 해당하는 키워드 조회
    @Override
    public Keyword findKeywordByIdAndCategoryType(Long keywordId, CategoryType categoryType) {

        return keywordRepository.findKeywordByIdAndCategoryType(keywordId, categoryType);

    }

    // 키워드 업데이트
    @Override
    @Transactional
    public KeywordResponse updateKeywords(KeywordUpdateRequest keywordUpdateRequest) {

        Long clientId = getClientIdFromAuthentication();
        Long accountId = getAccountIdFromAuthentication();

        // accountId와 clientId를 함께 검증
        Client client = clientRepository.findByIdAndAccountId(clientId, accountId)
                .orElseThrow(() -> new ApiException(ErrorStatus._CLIENT_FORBIDDEN));

        // Step 1: 기존 키워드와 연관된 기사 삭제
        articleRepository.deleteByClientId(clientId);

        // Step 2: 기존 키워드 삭제
        keywordRepository.deleteAllByClientId(clientId);

        // Step 3: 새로운 키워드 저장
        Map<CategoryType, List<String>> keywordsByCategory = keywordUpdateRequest.getKeywordsByCategory();
        for (Map.Entry<CategoryType, List<String>> entry : keywordsByCategory.entrySet()) {
            // 새로운 키워드 저장
            categoryServiceImpl.saveCategoryWithKeywords(entry.getKey(), entry.getValue(), client);
        }

        // Step 4: 저장된 키워드를 다시 조회
        List<Keyword> savedKeywords = keywordRepository.findKeywordByAccountIdAndClientId(accountId, clientId);

        // Step 5: 새로운 키워드에 대한 기사 저장
        for (Keyword keyword : savedKeywords) {
            saveArticlesForKeyword(keyword);
        }

        // 업데이트된 키워드 응답 반환
        return getKeywordResponses(accountId, clientId);

    }

    private Long getAccountIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAccountId();
    }

    private Account findAccountById() {
        return accountService.findAccountById(getAccountIdFromAuthentication());
    }

    private Long getClientIdFromAuthentication() {
        Account account = findAccountById();
        return account.getSelectedClientId();
    }


    private KeywordResponse getKeywordResponses(Long accountId, Long clientId) {
        // 갱신된 키워드 가져오기
        List<Keyword> keywords = findKeywordByAccountIdAndClientId(accountId, clientId);

        // CategoryType별로 키워드를 분류
        List<KeywordAndIdResponse> selfKeywords = getSELFKeywordAndIdResponses(keywords);

        List<KeywordAndIdResponse> competitorKeywords = getCOMPETITORKeywordAndIdResponses(keywords);

        List<KeywordAndIdResponse> industryKeywords = getINDUSTRYKeywordAndIdResponses(keywords);

        // KeywordResponse 생성 및 반환
        return KeywordResponse.builder()
                .self(selfKeywords)
                .competitor(competitorKeywords)
                .industry(industryKeywords)
                .build();

    }

    private List<Keyword> findKeywordByAccountIdAndClientId(Long accountId, Long clientId) {
        return keywordRepository.findKeywordByAccountIdAndClientId(accountId, clientId);
    }

    private List<KeywordAndIdResponse> getSELFKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.SELF)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .categoryType(CategoryType.SELF)
                        .build())
                .toList();
    }

    private List<KeywordAndIdResponse> getCOMPETITORKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.COMPETITOR)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .categoryType(CategoryType.COMPETITOR)
                        .build())
                .toList();
    }

    private List<KeywordAndIdResponse> getINDUSTRYKeywordAndIdResponses(List<Keyword> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.getCategory().getCategoryType() == CategoryType.INDUSTRY)
                .map(keyword -> KeywordAndIdResponse.builder()
                        .keywordId(keyword.getId())
                        .keywordName(keyword.getKeyword())
                        .categoryType(CategoryType.INDUSTRY)
                        .build())
                .toList();
    }

    @Transactional
    public void saveArticlesForKeyword(Keyword keyword) {
        saveArticlesFromGoogle(keyword);
    }

    private void saveArticlesFromGoogle(Keyword keyword) {
        ArticleResponse articleResponse = googleSearchService.toDto(keyword.getKeyword());

        for (ArticleGoogleDto dto : articleResponse.getGoogleArticles()) {

            articleRepository.save(dto.toEntity(keyword));
        }
    }

}
