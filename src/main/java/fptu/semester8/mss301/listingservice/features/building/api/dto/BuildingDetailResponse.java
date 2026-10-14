package fptu.semester8.mss301.listingservice.features.building.api.dto;

import java.math.BigDecimal;

public record BuildingDetailResponse(
    Long id,
    String name,
    String address,
    String status,
    BigDecimal latitude,
    BigDecimal longitude,
    Long createdAt,
    Long updatedAt
) {
}
