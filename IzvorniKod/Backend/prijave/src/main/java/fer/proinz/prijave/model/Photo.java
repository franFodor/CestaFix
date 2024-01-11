package fer.proinz.prijave.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Types;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int photoId;

    // Base64 String
    @Lob
    @Column(columnDefinition = "bytea")
    @JdbcTypeCode(Types.VARBINARY)
    private byte[] photoData;

    @JsonIgnoreProperties("photos")
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

}
