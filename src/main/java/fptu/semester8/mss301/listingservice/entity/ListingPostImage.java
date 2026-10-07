package fptu.semester8.mss301.listingservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "listing_post_image", schema = "listing-booking-service")
public class ListingPostImage {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "listing_post_id", nullable = false)
    private ListingPost listingPost;

    @Size(max = 500)
    @NotNull
    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Size(max = 1000)
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @NotNull
    @Column(name = "is_cover", nullable = false)
    private Boolean isCover;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Long createdAt;


}