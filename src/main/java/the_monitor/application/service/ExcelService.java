package the_monitor.application.service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.File;

public interface ExcelService {

    File createExcelFile(Long reportId);

    void generateExcel(Long reportId, HttpServletResponse response);
}
