package nii.ps.interfaceNSI.model;

import lombok.*;
import nii.ps.interfaceNSI.model.enums.AttrType;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DictionaryAttrModel {
    private String name;
    private String code;
    private AttrType attrType;
}