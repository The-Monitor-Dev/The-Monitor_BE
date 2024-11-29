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
@Table(name = "report_articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportArticle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_article_id")
    private Long id;

    @Column(name = "report_article_title", nullable = false)
    private String title;

    @Column(name = "report_article_keyword", nullable = false)
    private String keyword;

    @Column(name = "report_article_url", nullable = false, length = 1024)
    private String url;

    @Column(name = "report_article_publisher_name")
    private String publisherName;

    @Column(name = "report_article_reporter_name")
    private String reporterName;

    @Column(name = "report_article_publish_date", nullable = false)
    private String publishDate;

    @Column(name = "report_article_summary")
    private String summary;

    @Column(name = "report_article_category_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    @ManyToOne
    @JoinColumn(name = "report_category_id")
    private ReportCategory reportCategory;

    @Builder
    public ReportArticle(String title,
                         String keyword,
                         String url,
                         String publisherName,
                         String reporterName,
                         String publishDate,
                         String summary,
                         CategoryType categoryType,
                         ReportCategory reportCategory) {

        this.title = title;
        this.keyword = keyword;
        this.url = url;
        this.publisherName = publisherName;
        this.reporterName = reporterName;
        this.publishDate = publishDate;
        this.summary = summary;
        this.categoryType = categoryType;
        this.reportCategory = reportCategory;

    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }

    public void updateCategory(ReportCategory reportCategory) {
        this.reportCategory = reportCategory;
    }


    public void setReportCategory(ReportCategory reportCategory) {
        this.reportCategory = reportCategory;
    }

}
