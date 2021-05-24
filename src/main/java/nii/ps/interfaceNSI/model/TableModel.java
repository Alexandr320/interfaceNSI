package nii.ps.interfaceNSI.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;

import static nii.ps.interfaceNSI.model.ColorType.*;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class TableModel {
    private final List<String> columnNames;
    private final List<List> rows;

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(rows);
    }

    public void addButtonsPreview(final Function<List, String> urlFunc) {
        columnNames.add("");
        rows.forEach(row -> row.add(new ButtonModel("btn.preview", urlFunc.apply(row), INFO)));
    }
    public void addButtonsUpdate(final Function<List, String> urlFunc) {
        columnNames.add("");
        rows.forEach(row -> row.add(new ButtonModel("btn.edit", urlFunc.apply(row), WARNING)));
    }
    public void addButtonsDelete(final Function<List, String> urlFunc) {
        columnNames.add("");
        rows.forEach(row -> row.add(new ButtonModel("btn.delete", urlFunc.apply(row), DANGER)));
    }
}