package fptu.semester8.mss301.listingservice.features.room.domain;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "room", schema = "listing_booking_service")
public class Room {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Size(max = 50)
    @NotNull
    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @Column(name = "room_type_id")
    private UUID roomTypeId;

    @Size(max = 100)
    @Column(name = "room_type_name", length = 100)
    private String roomTypeName;

    @NotNull
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @NotNull
    @Column(name = "price", nullable = false, precision = 15)
    private BigDecimal price;

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
