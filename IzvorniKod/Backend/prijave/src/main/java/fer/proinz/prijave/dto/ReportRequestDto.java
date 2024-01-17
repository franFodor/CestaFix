package fer.proinz.prijave.dto;

import com.drew.lang.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDto {
    // Report fields
    private String title;
    private String description;
    private String address;
    private List<String> base64Photos;
    private String reportStatus;

    // Problem fields
    private Double latitude;
    private Double longitude;
    private String problemStatus;
    private int categoryId;
    private Integer mergeProblemId;

    public boolean hasCoordinates() {
        return this.getLatitude() != null && this.getLongitude() != null;
    }

    public boolean needsToConvertCoordinatesToAddress() {
        return this.getAddress() == null && this.hasCoordinates();
    }

    public boolean needsToConvertAddressToCoordinates() {
        return this.getAddress() != null && !this.hasCoordinates();
    }

    public boolean needsToGetLocationFromPhoto() {
        return this.getAddress() == null &&
                !this.hasCoordinates() &&
                this.getBase64Photos() != null;
    }

    public void setGeo(GeoLocation geoLocation) {
        this.setLatitude(geoLocation.getLatitude());
        this.setLongitude(geoLocation.getLongitude());
    }
}