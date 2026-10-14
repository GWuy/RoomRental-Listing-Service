package fptu.semester8.mss301.listingservice.features.building.repository;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface BuildingRepository extends JpaRepository<Building, Long>, JpaSpecificationExecutor<Building> {
    Page<Building> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    Optional<Building> findByIdAndDeletedAtIsNull(Long id);

}
