package fptu.semester8.mss301.listingservice.features.building.repository;

import fptu.semester8.mss301.listingservice.features.building.domain.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WardRepository extends JpaRepository<Ward, UUID> {
    List<Ward> findAllByOrderByNameAsc();

    List<Ward> findAllByProvinceIdOrderByNameAsc(UUID provinceId);

    boolean existsByProvinceId(UUID provinceId);

    boolean existsByCode(Integer code);

    boolean existsByCodeAndIdNot(Integer code, UUID id);
}
