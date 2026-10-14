package fptu.semester8.mss301.listingservice.features.building.domain;

import fptu.semester8.mss301.listingservice.common.id.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "building", schema = "listing_booking_service")
public class Building {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 500)
    @NotNull
    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Size(max = 30)
    @NotNull
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;



    @jakarta.persistence.PrePersist
    private void assignSnowflakeId() {
        if (id == null) {
            id = SnowflakeIdGenerator.nextId();
        }
    }
}
