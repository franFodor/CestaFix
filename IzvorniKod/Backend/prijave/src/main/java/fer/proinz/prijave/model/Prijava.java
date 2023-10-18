package fer.proinz.prijave.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Prijava {

    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private Timestamp createTime;
    private Timestamp updateTime;


}
