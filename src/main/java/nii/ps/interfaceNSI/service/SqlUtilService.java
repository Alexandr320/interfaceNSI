package nii.ps.interfaceNSI.service;

import nii.ps.interfaceNSI.model.enums.DataType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SqlUtilService {

    public List<Map<String, Object>> createParamList(final List<List> rows, final List<String> columns) {
        final List<String> realColumns =
                isTitleRow(rows.get(0), columns)
                        ? rows.remove(0)
                        : columns;

        return rows.stream().map(row -> {
            Map<String, Object> params = new HashMap<>();
            for (int index = 0; index < realColumns.size(); index++) {
                String colName = realColumns.get(index);
                Object value = index < row.size() ? row.get(index) : "";
                params.put(colName, value);
            }
            return params;
        }).collect(Collectors.toList());
    }

    private boolean isTitleRow(final List row, final Collection<String> colNames) {
        return colNames.contains(row.get(0));
    }

    public void changeTypes(List<Map<String, Object>> paramList, Map<String, DataType> typeMap) {
        for (Map<String, Object> params : paramList) {
            params.replaceAll((key, value) -> typeMap.containsKey(key) ? typeMap.get(key).parse(value) : value);
        }
    }

}
