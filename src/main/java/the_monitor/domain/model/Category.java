package the_monitor.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;
import the_monitor.domain.enums.CategoryType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Keyword> keywords = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @Builder
    public Category(CategoryType categoryType,
                    Client client) {

        this.categoryType = categoryType;
        this.client = client;

    }

    public void addKeywords(List<Keyword> newKeywords) {
        this.keywords.addAll(newKeywords); // Category의 keywords 리스트에 추가
        for (Keyword keyword : newKeywords) {
            keyword.setCategory(this); // 각 Keyword의 카테고리도 현재 Category로 설정
        }
    }

}
