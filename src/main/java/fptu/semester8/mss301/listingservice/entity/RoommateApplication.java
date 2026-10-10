package fptu.semester8.mss301.listingservice.entity;

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
@Table(name = "roommate_application", schema = "listing_booking_service")
public class RoommateApplication {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "roommate_post_id", nullable = false)
    private RoommatePost roommatePost;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @Size(max = 40)
    @NotNull
    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;


}