package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

@Getter
@Entity
@Table(name = "articles")
@NoArgsConstructor
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(name = "article_title", nullable = false)
    private String title;

    @Column(name = "article_body", nullable = false)
    private String body;

    @Column(name = "article_url", nullable = false, length = 1024)
    private String url;

    @Column(name = "article_image_url", length = 1024)
    private String imageUrl;

    @Column(name = "article_publisher_name")
    private String publisherName;

    @Column(name = "article_publish_date")
    private String publishDate;

    @Column(name = "article_reporter_name")
    private String reporterName;

    @Column(name = "article_is_read", nullable = false)
    private boolean read = false;

    @Column(name = "article_is_added", nullable = false)
    private boolean added = false;

    @Column(name = "article_is_scrapped", nullable = false)
    private boolean scrapped = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @Builder
    public Article(String title,
                   String body,
                   String url,
                   String imageUrl,
                   String publisherName,
                   String publishDate,
                   String reporterName,
                   boolean read,
                   boolean added,
                   boolean scrapped,
                   Keyword keyword) {

        this.title = title;
        this.body = body;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publisherName = publisherName;
        this.publishDate = publishDate;
        this.reporterName = reporterName;
        this.read = read;
        this.added = added;
        this.scrapped = scrapped;
        this.keyword = keyword;

    }

    public void setScrapStatus(boolean scrapped) {
        this.scrapped = scrapped;
    }

    public void setAddedStatus(boolean added) {
        this.added = added;
    }

    public void setReadStatus(boolean read) {
        this.read = read;
    }

}
