package nii.ps.interfaceNSI.service;

import nii.ps.interfaceNSI.model.TableModel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class CsvService {
    private static final String CELL_SEPARATOR = ";";
    private static final String ROW_SEPARATOR = "\r\n";

    public void generateAndWriteToStream(TableModel tableModel, OutputStream outputStream) throws IOException {
        final StringBuilder builder = new StringBuilder();

        // Заголовки столбцов
        tableModel.getColumnNames().forEach(colName -> builder.append(colName).append(CELL_SEPARATOR));
        builder.append(ROW_SEPARATOR);

        // Строки результата
        tableModel.getRows().forEach(elements -> {
            elements.forEach(elem -> builder.append(elem != null ? elem.toString() : "").append(CELL_SEPARATOR));
            builder.append(ROW_SEPARATOR);
        });

        outputStream.write(builder.toString().getBytes(Charset.forName("cp1251")));
    }

}
