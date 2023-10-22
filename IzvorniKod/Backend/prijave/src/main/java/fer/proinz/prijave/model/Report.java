package fer.proinz.prijave.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Reports")
public class Report implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reportId;

    private String title;
    private String description;
    private String locationCoordinates;
    private String address;
    private byte[] photo;
    private Timestamp reportTime;
    private String status;

    public Report(){}

    public static class builder {
        private long reportId;
        private String title;
        private String description;
        private String locationCoordinates;
        private String address;
        private byte[] photo;
        private Timestamp reportTime;
        private String status;

        public builder reportId(long reportId) {
            this.reportId = reportId;
            return this;
        }

        public builder title(String title) {
            this.title = title;
            return this;
        }

        public builder description(String description) {
            this.description = description;
            return this;
        }

        public builder locationCoordinates(String locationCoordinates) {
            this.locationCoordinates = locationCoordinates;
            return this;
        }

        public builder address(String address) {
            this.address = address;
            return this;
        }

        public builder photo(byte[] photo) {
            this.photo = photo;
            return this;
        }

        public builder reportTime(Timestamp reportTime) {
            this.reportTime = reportTime;
            return this;
        }

        public builder status(String status) {
            this.status = status;
            return this;
        }

        public Report build() {
            Report report = new Report();
            report.reportId = this.reportId;
            report.title = this.title;
            report.description = this.description;
            report.locationCoordinates = this.locationCoordinates;
            report.address = this.address;
            report.photo = this.photo;
            report.reportTime = this.reportTime;
            report.status = this.status;
            return report;
        }
    }


}


