package the_monitor.application.serviceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import the_monitor.application.service.ExcelService;
import the_monitor.domain.enums.CategoryType;
import the_monitor.domain.model.*;
import the_monitor.domain.repository.ReportRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExcelServiceImpl implements ExcelService {

    private final ReportRepository reportRepository;

    // 엑셀 경로 수정
    private static final String TEMPLATE_PATH = "/templates/templates.xlsx";

    @Override
    public File createExcelFile(Long reportId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        List<ReportCategory> reportCategories = report.getReportCategories();

        try (InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (templateStream == null) {
                throw new IllegalStateException("Excel template not found");
            }

            Workbook workbook = new XSSFWorkbook(templateStream);
            Sheet sheet = workbook.getSheetAt(0);

            // B2: Client 이름
            setCellValue(sheet, 1, 1, report.getClient().getName());

            // B3: Report Title
            setCellValue(sheet, 2, 1, report.getTitle());

            int currentRow = 6; // SELF 기사 시작 위치 (B7)

            for (CategoryType categoryType : CategoryType.values()) {
                List<ReportCategory> categories = reportCategories.stream()
                        .filter(category -> category.getCategoryType() == categoryType)
                        .toList();

                if (categories.isEmpty()) continue;

                // 카테고리 타이틀 삽입
                if (categoryType != CategoryType.SELF) {
                    currentRow++;
                    mergeCells(sheet, currentRow, currentRow, 1, 7);

                    // 카테고리명 설정
                    String categoryTitle = switch (categoryType) {
                        case COMPETITOR -> "경쟁사";
                        case INDUSTRY -> "산업";
                        default -> categoryType.name();
                    };
                    setCellValue(sheet, currentRow, 1, categoryTitle);

                    currentRow++; // 카테고리 타이틀 아래로 이동
                }

                // 각 카테고리에서 번호 초기화
                int categoryNumber = 1;

                // 카테고리 기사 데이터 삽입
                for (ReportCategory category : categories) {
                    for (ReportArticle article : category.getReportArticles()) {
                        // 기사 삽입
                        setCellValue(sheet, currentRow, 1, String.valueOf(categoryNumber)); // 번호
                        setCellValue(sheet, currentRow, 2, formatDate(article.getPublishDate())); // 날짜
                        setCellValue(sheet, currentRow, 3, article.getKeyword());          // 키워드
                        setCellValue(sheet, currentRow, 4, article.getTitle());            // 제목
                        setCellValue(sheet, currentRow, 5, article.getUrl());              // URL
                        setCellValue(sheet, currentRow, 6, article.getPublisherName());    // Publisher
                        currentRow++;
                        categoryNumber++; // 번호 증가
                    }
                }
            }

            // 로컬 임시 파일 생성
            File tempFile = File.createTempFile("report_" + reportId, ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }

            return tempFile;

        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }

    }

    @Override
    public void generateExcel(Long reportId, HttpServletResponse response) {

        try {
            File excelFile = createExcelFile(reportId); // 파일 생성

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=report.xlsx");

            // 파일을 HTTP 응답으로 전송
            try (OutputStream outputStream = response.getOutputStream();
                 FileInputStream fis = new FileInputStream(excelFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // 로컬 파일 삭제
            if (excelFile.exists()) {
                excelFile.delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 다운로드 중 오류 발생", e);
        }

    }

    // 셀 병합 유틸리티
    private void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {

        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);

        Workbook workbook = sheet.getWorkbook();
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true); // Bold 처리
        font.setFontHeightInPoints((short) 14);
        font.setFontName("맑은 고딕");
        style.setFont(font);

        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        Cell cell = row.getCell(firstCol);
        if (cell == null) {
            cell = row.createCell(firstCol);
        }
        cell.setCellStyle(style);
    }

    // 셀 값 설정 유틸리티
    private void setCellValue(Sheet sheet, int rowNumber, int columnNumber, String value) {
        Row row = sheet.getRow(rowNumber);
        if (row == null) {
            row = sheet.createRow(rowNumber);
        }
        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }
        cell.setCellValue(value);
    }

    private String formatDate(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null; // 날짜가 없을 경우 기본값 반환
        }

        // 지원할 날짜 형식 리스트
        String[] patterns = {
                "yyyy.MM.dd HH:mm",
                "yyyy-MM-dd'T'HH:mm:ssXXX", // ISO 8601 형식
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };
        for (String pattern : patterns) {
            try {
                // 패턴에 맞는 날짜를 반환
                return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception ignored) {
                // 패턴이 맞지 않을 경우 무시하고 다음으로 진행
            }
        }
        // 모든 패턴에 맞지 않는 경우 기본값 반환
        return "유효하지 않은 날짜";
    }

}
