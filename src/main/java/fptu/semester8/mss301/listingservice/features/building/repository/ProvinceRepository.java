package fptu.semester8.mss301.listingservice.features.building.repository;

import fptu.semester8.mss301.listingservice.features.building.domain.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProvinceRepository extends JpaRepository<Province, UUID> {
    List<Province> findAllByOrderByNameAsc();

    boolean existsByCode(Integer code);

    boolean existsByCodeAndIdNot(Integer code, UUID id);
}
