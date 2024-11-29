package the_monitor.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReportListResponse {

    private Long reportId;
    private String title;
    private String createdAt;
    private String updatedAt;


    @Builder
    public ReportListResponse(Long reportId, String title, String createdAt, String updatedAt) {

        this.reportId = reportId;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

}
