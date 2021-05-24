package nii.ps.interfaceNSI.model;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ButtonModel {
    private final String text;
    private final String href;
    private ColorType colorType;
}
