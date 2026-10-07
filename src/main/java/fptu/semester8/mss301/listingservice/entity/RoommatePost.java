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
@Table(name = "roommate_post", schema = "listing-booking-service")
public class RoommatePost {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @NotNull
    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @NotNull
    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "budget_share", nullable = false, precision = 15)
    private BigDecimal budgetShare;

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