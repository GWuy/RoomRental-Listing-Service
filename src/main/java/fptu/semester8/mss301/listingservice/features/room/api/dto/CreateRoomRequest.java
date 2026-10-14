package fptu.semester8.mss301.listingservice.features.room.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateRoomRequest(
    @NotNull Long buildingId,
    @NotBlank @Size(max = 50) String roomNumber,
    Long roomTypeId,
    @Size(max = 100) String roomTypeName,
    @NotNull @Positive Integer maxCapacity,
    @NotNull @DecimalMin("0.0") @Digits(integer = 15, fraction = 0) BigDecimal price,
    @NotBlank @Size(max = 30) String status
) {
}
