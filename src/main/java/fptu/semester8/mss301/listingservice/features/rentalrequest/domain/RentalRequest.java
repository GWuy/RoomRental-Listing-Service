package fptu.semester8.mss301.listingservice.features.rentalrequest.domain;

import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPost;
import fptu.semester8.mss301.listingservice.features.listing.domain.RoomListing;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "rental_request", schema = "listing_booking_service")
public class RentalRequest {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private RoomListing listing;

    @NotNull
    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @NotNull
    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Size(max = 30)
    @NotNull
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @NotNull
    @Column(name = "move_in_date", nullable = false)
    private Long moveInDate;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;

    @Column(name = "reject_reason", length = Integer.MAX_VALUE)
    private String rejectReason;

    @NotNull
    @Column(name = "requested_at", nullable = false)
    private Long requestedAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "listing_post_id", nullable = false)
    private ListingPost listingPost;


}
