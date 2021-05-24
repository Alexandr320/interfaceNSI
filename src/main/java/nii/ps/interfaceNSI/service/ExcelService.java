package nii.ps.interfaceNSI.service;

import nii.ps.interfaceNSI.model.TableModel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ExcelService {

    public void generateAndWriteToStream(TableModel tableModel, OutputStream outputStream) throws IOException {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet("Table Data");
            sheet.setDefaultColumnWidth(30);
            HSSFCellStyle boldCenterStyle = getBoldCenterStyle(workbook);
            HSSFCellStyle centerStyle = getCenterStyle(workbook);

            // Заголовки столбцов
            HSSFRow titleRow = sheet.createRow(0);
            addCells(titleRow, tableModel.getColumnNames(), boldCenterStyle);

            // Строки результата
            for (int i = 0; i < tableModel.getRows().size(); i++) {
                List elements = tableModel.getRows().get(i);
                int rowIndex = i + 1;   // Т.к. строки начинаются с 1, а не с 0 (нулевая строка - заголовки)

                HSSFRow row = sheet.createRow(rowIndex);
                addCells(row, elements, centerStyle);
            }

            workbook.write(outputStream);
        }
    }

    private void addCells(HSSFRow row, List elements, HSSFCellStyle style) {
        for (int i = 0; i < elements.size(); i++) {
            Object value = elements.get(i);
            HSSFCell cell;
            if (value == null) {
                cell = row.createCell(i, CellType.STRING);
                cell.setCellValue("");
            } else if (value instanceof Number) {
                cell = row.createCell(i, CellType.NUMERIC);
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Boolean) {
                cell = row.createCell(i, CellType.BOOLEAN);
                cell.setCellValue((Boolean) value);
            } else {
                cell = row.createCell(i, CellType.STRING);
                cell.setCellValue(value.toString());
            }

            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }

    private HSSFCellStyle getBoldCenterStyle(HSSFWorkbook workbook) {
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        HSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        return titleStyle;
    }

    private HSSFCellStyle getCenterStyle(HSSFWorkbook workbook) {
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        return titleStyle;
    }

}
