package fer.proinz.prijave.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Reports")
public class Report implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reportId;

    @Column(name = "business_id", columnDefinition = "uuid", nullable = false, updatable = false, insertable = false)
    private UUID businessId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Nullable
    private User user;

    private String title;

    private String description;

    private String address;

    private byte[] photo;

    @Column(name = "report_time", nullable = false, updatable = false, insertable = false)
    private Timestamp reportTime;

    private String status;

    private double longitude;

    private double latitude;

}


