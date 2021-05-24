package nii.ps.interfaceNSI.model;

import lombok.*;

import java.util.List;

/*
    Справочник
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryModel {
    private String name;
    private String description;
    private List<DictionaryAttrModel> attrs;
}