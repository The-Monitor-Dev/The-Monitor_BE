package the_monitor.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import the_monitor.application.service.ExcelService;

@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping("/generate")
    public void generateExcel(@RequestParam("reportId") Long reportId, HttpServletResponse response) {
        excelService.generateExcel(reportId, response);
    }

}
