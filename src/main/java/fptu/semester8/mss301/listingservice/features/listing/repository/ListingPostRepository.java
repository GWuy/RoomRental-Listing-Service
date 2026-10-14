package fptu.semester8.mss301.listingservice.features.listing.repository;

import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPost;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ListingPostRepository extends JpaRepository<ListingPost, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select post from ListingPost post where post.id = :id")
    Optional<ListingPost> findByIdForUpdate(@Param("id") Long id);
}
