package fer.proinz.prijave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestDto {
    // Report fields
    private String title;
    private String description;
    private String address;
    private List<String> base64Photos;
    private String reportStatus;

    // Problem fields
    private Double longitude;
    private Double latitude;
    private String problemStatus;
    private int categoryId;
    private Integer mergeProblemId;
}