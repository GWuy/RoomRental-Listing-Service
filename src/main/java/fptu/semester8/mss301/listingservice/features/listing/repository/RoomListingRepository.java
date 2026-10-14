package fptu.semester8.mss301.listingservice.features.listing.repository;

import fptu.semester8.mss301.listingservice.features.listing.domain.RoomListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomListingRepository extends JpaRepository<RoomListing, Long> {
    boolean existsByRoomId(Long roomId);
}
