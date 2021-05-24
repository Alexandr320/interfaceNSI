package nii.ps.interfaceNSI.model.enums;

public enum AttrType {
    LOGIC_1,
    TABLE_ELEMENT_2,
    PARENT_CHILD_3,
    DATE_4,
    DATE_TIME_5,
    FRACTION_6,
    INT_32_8,
    STRING_9,
    TEXT_13,
    PICTURE_15,
    ELEM_TABLE_ARR_17,
    TIME_27,
    INT_64_30,
    ;

   public int getId() {
       int startIndex = name().lastIndexOf("_") + 1;
       String idStr = name().substring(startIndex);
       return Integer.parseInt(idStr);
   }
}