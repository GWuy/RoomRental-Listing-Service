package fptu.semester8.mss301.listingservice.features.listing.repository;

import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListingPostImageRepository extends JpaRepository<ListingPostImage, Long> {
    Optional<ListingPostImage> findByIdAndListingPost_Id(Long id, Long listingPostId);

    List<ListingPostImage> findAllByListingPost_Id(Long listingPostId);

    Optional<ListingPostImage> findByListingPost_IdAndIsCoverTrue(Long listingPostId);
}
