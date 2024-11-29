package the_monitor.application.service;

import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.Category;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Keyword;

import java.util.List;

public interface CategoryService {
    // 카테고리 생성 및 키워드 저장
    void saveCategoryWithKeywords(CategoryType categoryType, List<String> keywords, Client client);

    // 키워드 생성
    List<Keyword> createKeywords(List<String> keywords, Category category);
}