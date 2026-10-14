package fptu.semester8.mss301.listingservice.features.building.repository;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BuildingRepository extends JpaRepository<Building, Long> {
}
