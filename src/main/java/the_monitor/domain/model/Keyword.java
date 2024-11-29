package the_monitor.domain.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import the_monitor.common.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@Table(name = "keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "keyword_result_count")
    private int resultCount;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @Builder
    public Keyword(String keyword,
                   Category category,
                   List<Article> articles,
                   int resultCount) {

        this.keyword = keyword;
        this.category = category;
        this.articles = articles;
        this.resultCount = resultCount;

    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getKeyword() {
        return this.keyword;
    }

}
