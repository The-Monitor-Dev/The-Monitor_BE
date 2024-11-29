package the_monitor.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CategoryType {
    SELF,          // 자회사
    COMPETITOR,    // 경쟁사
    INDUSTRY       // 산업
}