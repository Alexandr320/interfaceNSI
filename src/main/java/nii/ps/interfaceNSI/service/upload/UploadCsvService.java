package nii.ps.interfaceNSI.service.upload;

import nii.ps.interfaceNSI.model.enums.DataType;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UploadCsvService {
    private static final String CELL_SEPARATOR = ";";
    private static final String ROW_SEPARATOR = "\n";

    public List<List> parse(InputStream inputStream) throws IOException {
        byte[] sourceBytes = new byte[inputStream.available()];
        IOUtils.readFully(inputStream, sourceBytes);
        String sourceString = new String(sourceBytes, Charset.forName("cp1251"));

        return Arrays.stream(sourceString.split(ROW_SEPARATOR))
                .map(String::trim)
                .filter(row -> !StringUtils.isEmpty(row))
                .map(row -> row.split(CELL_SEPARATOR))
                .map(row -> Arrays.asList(row))
                .collect(Collectors.toList());
    }
}
