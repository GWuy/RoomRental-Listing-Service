package fptu.semester8.mss301.listingservice.features.room.repository;

import fptu.semester8.mss301.listingservice.features.room.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findAllByBuilding_DeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByBuilding_IdAndRoomNumber(Long buildingId, String roomNumber);

    @Query("select r from Room r join r.building b where b.deletedAt is null and " +
        "(lower(r.roomNumber) like lower(concat('%', :keyword, '%')) " +
        "or lower(coalesce(r.roomTypeName, '')) like lower(concat('%', :keyword, '%')) " +
        "or lower(b.name) like lower(concat('%', :keyword, '%')) " +
        "or lower(b.address) like lower(concat('%', :keyword, '%'))) " +
        "order by r.createdAt desc")
    Page<Room> search(@Param("keyword") String keyword, Pageable pageable);
}
