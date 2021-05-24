package nii.ps.interfaceNSI.model.enums;

public enum FileExtension {
    XLS,
    XLSX,
    CSV,
    OTHER,
    ;

    public static FileExtension parseFromFilename(String filename) {
        try {
            return valueOf(filename.substring(filename.lastIndexOf(".") + 1).toUpperCase());
        } catch (Exception e) {
            return OTHER;
        }
    }
}
