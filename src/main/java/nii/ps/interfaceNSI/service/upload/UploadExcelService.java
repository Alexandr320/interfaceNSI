package nii.ps.interfaceNSI.service.upload;

import nii.ps.interfaceNSI.model.enums.FileExtension;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadExcelService {

    /**
     *
     * @param inputStream - поток файла для чтения
     * @param fileExtension - допускаются значения только XLS и XLSX
     */
    public List<List> parse(InputStream inputStream, FileExtension fileExtension) throws IOException {
        Preconditions.checkArgument(
                fileExtension == FileExtension.XLS || fileExtension == FileExtension.XLSX,
                "fileExtension"
        );

        try (Workbook workbook = fileExtension == FileExtension.XLSX ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream)) {
            final List<List> result = new ArrayList<>();

            workbook.sheetIterator().next().rowIterator().forEachRemaining(row -> {
                final List resultRow = new ArrayList();
                row.cellIterator().forEachRemaining(cell -> resultRow.add(getValue(cell)));
                result.add(resultRow);
            });

            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    private Object getValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return getLongOrDouble(cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return cell.getStringCellValue();
        }
    }

    private Number getLongOrDouble(Number number) {
        return number.doubleValue() % 1 == 0 ? number.longValue() : number.doubleValue();
    }

}
