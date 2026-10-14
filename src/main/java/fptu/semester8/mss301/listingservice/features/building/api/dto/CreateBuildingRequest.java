package fptu.semester8.mss301.listingservice.features.building.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateBuildingRequest(
    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must be at most 255 characters")
    String name,

    @NotBlank(message = "address must not be blank")
    @Size(max = 500, message = "address must be at most 500 characters")
    String address,

    @DecimalMin(value = "-90.0", message = "latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "latitude must be at most 90")
    @Digits(integer = 2, fraction = 8, message = "latitude must have at most 8 decimal places")
    BigDecimal latitude,

    @DecimalMin(value = "-180.0", message = "longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "longitude must be at most 180")
    @Digits(integer = 3, fraction = 8, message = "longitude must have at most 8 decimal places")
    BigDecimal longitude
) {
}
