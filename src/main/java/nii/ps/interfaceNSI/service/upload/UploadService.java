package nii.ps.interfaceNSI.service.upload;

import nii.ps.interfaceNSI.model.enums.DataType;
import nii.ps.interfaceNSI.model.enums.FileExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class UploadService {
    @Autowired
    private UploadCsvService uploadCsvService;
    @Autowired
    private UploadExcelService uploadExcelService;

    public List<List> parse(InputStream inputStream, String filename) throws IOException {
        FileExtension fileExtension = FileExtension.parseFromFilename(filename);
        switch (fileExtension) {
            case CSV:
                return uploadCsvService.parse(inputStream);
            case XLS:
                return uploadExcelService.parse(inputStream, FileExtension.XLS);
            case XLSX:
                return uploadExcelService.parse(inputStream, FileExtension.XLSX);
            default:
                throw new IllegalArgumentException(String.format(
                        "Error parsing %s. File extension must be: xls, xlsx, csv.", filename));
        }
    }
}
