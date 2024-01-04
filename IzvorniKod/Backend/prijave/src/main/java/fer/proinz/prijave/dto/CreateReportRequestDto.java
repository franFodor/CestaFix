package fer.proinz.prijave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestDto {
    // Report fields
    private String title;
    private String description;
    private String address;
    private byte[] photo;
    private String reportStatus;

    // Problem fields
    private Double problemLongitude;
    private Double problemLatitude;
    private String problemStatus;
    private int categoryId;
}