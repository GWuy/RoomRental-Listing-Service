package fptu.semester8.mss301.listingservice.features.building.api.dto;

import java.util.UUID;

public record WardDetailResponse(
    UUID id,
    Integer code,
    String name,
    String codename,
    String divisionType,
    Long createdAt,
    Long updatedAt,
    ProvinceSummaryResponse province
) {
}
