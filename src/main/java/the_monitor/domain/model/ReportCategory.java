package the_monitor.domain.model;

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
@Table(name = "report_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_category_id")
    private Long id;

    @Column(name = "report_category_type", nullable = false)
    private CategoryType categoryType;

    @Column(name = "report_category_name", nullable = false)
    private String name;

    @Column(name = "report_category_description", nullable = true)
    private String description;

    @Column(name = "report_category_is_default")
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @OneToMany(mappedBy = "reportCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReportArticle> reportArticles = new ArrayList<>();

    @Builder
    public ReportCategory(CategoryType categoryType,
                          String name,
                          String description,
                          Report report,
                          List<ReportArticle> reportArticles,
                          boolean isDefault) {

        this.categoryType = categoryType;
        this.name = name;
        this.description = description;
        this.report = report;
        this.reportArticles = reportArticles;
        this.isDefault = isDefault;

    }

    public void addReportArticles(List<ReportArticle> reportArticles) {
        this.reportArticles = reportArticles; // 리스트에 개별 ReportArticle 추가
        reportArticles.forEach(reportArticle -> reportArticle.setReportCategory(this)); // 양방향 연관 관계 설정
    }

    public void addReportArticle(ReportArticle reportArticle) {
        this.reportArticles.add(reportArticle); // 리스트에 개별 ReportArticle 추가
        reportArticle.setReportCategory(this); // 양방향 연관 관계 설정
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public void setReport(Report report) {
        this.report = report;
    }

}