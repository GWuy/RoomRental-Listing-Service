package fptu.semester8.mss301.listingservice.features.building.repository;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BuildingRepository extends JpaRepository<Building, Long> {
    Page<Building> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    java.util.Optional<Building> findByIdAndDeletedAtIsNull(Long id);

    @Query("select b from Building b where b.deletedAt is null " +
        "and (:name is null or lower(b.name) like lower(concat('%', :name, '%'))) " +
        "and (:address is null or lower(b.address) like lower(concat('%', :address, '%'))) " +
        "order by b.createdAt desc")
    Page<Building> search(@Param("name") String name, @Param("address") String address, Pageable pageable);
}
