package fer.proinz.prijave.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Reports")
public class Report implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;

    @Column(name = "business_id", columnDefinition = "uuid", nullable = false, updatable = false, insertable = false)
    private UUID businessId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Nullable
    private User user;

    private String title;

    private String description;

    private String address;

    @JsonIgnore
    @JsonIgnoreProperties("report")
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    private List<Photo> photos = new ArrayList<>();

    @Transient
    private List<String> base64Photos;

    @Column(name = "report_time", nullable = false, updatable = false, insertable = false)
    private Timestamp reportTime;

    private String status;

    private Double latitude;

    private Double longitude;

    @JsonIgnoreProperties("reports")
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        return Objects.equals(reportId, report.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId);
    }

}


