package fptu.semester8.mss301.listingservice.features.room.api.dto;

import java.math.BigDecimal;

public record RoomDetailResponse(
    Long id,
    Long buildingId,
    String roomNumber,
    Long roomTypeId,
    String roomTypeName,
    Integer maxCapacity,
    BigDecimal price,
    String status,
    Long createdAt,
    Long updatedAt
) {
}
