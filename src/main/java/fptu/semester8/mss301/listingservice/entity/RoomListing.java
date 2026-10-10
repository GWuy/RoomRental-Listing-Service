package fptu.semester8.mss301.listingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "room_listing", schema = "listing_booking_service")
public class RoomListing {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @NotNull
    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Size(max = 255)
    @NotNull
    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Size(max = 500)
    @NotNull
    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "ward_id")
    private UUID wardId;

    @Column(name = "district_id")
    private UUID districtId;

    @Column(name = "room_type_id")
    private UUID roomTypeId;

    @Size(max = 100)
    @Column(name = "room_type_name", length = 100)
    private String roomTypeName;

    @Size(max = 50)
    @NotNull
    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @NotNull
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @NotNull
    @Column(name = "price", nullable = false, precision = 15)
    private BigDecimal price;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Size(max = 30)
    @NotNull
    @Column(name = "room_status", nullable = false, length = 30)
    private String roomStatus;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 30)
    @NotNull
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;


}