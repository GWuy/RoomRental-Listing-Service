package fptu.semester8.mss301.listingservice.features.building.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateBuildingWithRoomsRequest(
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Size(max = 500) String address,
    @DecimalMin("-90.0") @DecimalMax("90.0") @Digits(integer = 2, fraction = 8) BigDecimal latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") @Digits(integer = 3, fraction = 8) BigDecimal longitude,
    @NotEmpty List<@Valid CreateBuildingRoomRequest> rooms
) {
}
