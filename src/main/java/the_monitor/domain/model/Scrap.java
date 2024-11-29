package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;
import the_monitor.domain.enums.CategoryType;

@Entity
@Getter
@Table(name = "scraps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @Column(name = "scrap_original_article_id", nullable = false)
    private Long originalArticleId;

    @Column(name = "scrap_title", nullable = false)
    private String title;

    @Column(name = "scrap_url", nullable = false, length = 1024)
    private String url;

    @Column(name = "scrap_keyword", nullable = false)
    private String keyword;

    @Column(name = "scrap_publisher_name", nullable = false)
    private String publisherName;

    @Column(name = "scrap_reporter_name", nullable = false)
    private String reporterName;

    @Column(name = "scrap_publish_date", nullable = false)
    private String publishDate;

    @Column(name = "scrap_category_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Builder
    public Scrap(Long originalArticleId,
                 String title,
                 String url,
                 String keyword,
                 String publisherName,
                 String reporterName,
                 String publishDate,
                 CategoryType categoryType,
                 Client client) {

        this.originalArticleId = originalArticleId;
        this.title = title;
        this.url = url;
        this.keyword = keyword;
        this.publisherName = publisherName;
        this.reporterName = reporterName;
        this.publishDate = publishDate;
        this.categoryType = categoryType;
        this.client = client;

    }

}
