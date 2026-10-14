package fptu.semester8.mss301.listingservice.features.building.repository;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BuildingSpecifications {
    private static final char LIKE_ESCAPE = '\\';

    private BuildingSpecifications() {
    }

    public static Specification<Building> search(String name, String address) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (name != null) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.<String>get("name")), containsPattern(name), LIKE_ESCAPE));
            }
            if (address != null) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.<String>get("address")), containsPattern(address), LIKE_ESCAPE));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String containsPattern(String value) {
        String escaped = value.toLowerCase(Locale.ROOT)
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
