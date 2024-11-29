package the_monitor.application.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.service.CategoryService;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Category;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Keyword;
import the_monitor.domain.repository.CategoryRepository;
import the_monitor.domain.repository.KeywordRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final KeywordRepository keywordRepository;

    // 카테고리와 키워드 저장
    @Override
    public void saveCategoryWithKeywords(CategoryType categoryType,
                                         List<String> keywords,
                                         Client client) {

        // 카테고리 생성
        Category category = Category.builder()
                .categoryType(categoryType)
                .client(client)
                .build();
        categoryRepository.save(category);

        // 키워드 생성
        List<Keyword> keywordEntities = createKeywords(keywords, category);
        keywordRepository.saveAll(keywordEntities);

        category.addKeywords(keywordEntities);

    }

    // 키워드 리스트를 Keyword 엔티티로 변환
    @Override
    public List<Keyword> createKeywords(List<String> keywords, Category category) {

        // 키워드 리스트를 Keyword 엔티티로 변환
        return keywords.stream()
                .map(keyword -> Keyword.builder()
                        .keyword(keyword)
                        .category(category)
                        .resultCount(0) // 초기 resultCount 설정
                        .build())
                .collect(Collectors.toList());

    }

}