package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "report_title", nullable = false)
    private String title;

    @Column(name = "report_logo", nullable = false)
    private String logo;

    @Column(name = "report_color", nullable = false)
    private String color;

    @Column(name = "report_is_media", nullable = false)
    private boolean media;

    @Column(name = "report_is_reporter", nullable = false)
    private boolean reporter;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReportCategory> reportCategories = new ArrayList<>();;

    @Builder
    public Report(String title,
                  Client client,
                  String logo,
                  String color,
                  List<ReportCategory> reportCategories,
                  boolean media,
                  boolean reporter) {

        this.title = title;
        this.client = client;
        this.logo = logo;
        this.color = color;
        this.reportCategories = reportCategories;
        this.media = media;
        this.reporter = reporter;

    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateLogo(String logo) {
        this.logo = logo;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void updateReportOptions(boolean media, boolean reporter) {
        this.media = media;
        this.reporter = reporter;
    }

    public void addReportCategories(List<ReportCategory> reportCategories) {
        this.reportCategories = reportCategories;
        reportCategories.forEach(reportCategory -> reportCategory.setReport(this));
    }

    public void addReportCategory(ReportCategory category) {
        this.reportCategories.add(category);
    }

}
