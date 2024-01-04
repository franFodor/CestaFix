package fer.proinz.prijave.dto;

import lombok.Data;
@Data
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