package nii.ps.interfaceNSI.model.enums;

public enum DataType {
    LONG,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    STRING,
    ;

    public Object parse(Object value) {
        if (value == null) {
            return null;
        }
        switch (this) {
            case LONG:
                return Long.valueOf(value.toString().trim());
            case INTEGER:
                return Integer.valueOf(value.toString().trim());
            case DOUBLE:
                return Double.valueOf(value.toString().trim());
            case BOOLEAN:
                return Boolean.valueOf(value.toString().trim());
            default:
                return value;
        }
    }
}
