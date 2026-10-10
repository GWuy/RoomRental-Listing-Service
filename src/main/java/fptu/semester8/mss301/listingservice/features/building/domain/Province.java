package fptu.semester8.mss301.listingservice.features.building.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "province", schema = "listing_booking_service")
public class Province {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private Integer code;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "codename")
    private String codename;

    @Size(max = 100)
    @Column(name = "division_type", length = 100)
    private String divisionType;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
}
