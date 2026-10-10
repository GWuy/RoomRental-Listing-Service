package fptu.semester8.mss301.listingservice.features.building.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record WardWriteRequest(
    @NotNull UUID provinceId,
    @NotNull @Positive Integer code,
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) String codename,
    @Size(max = 100) String divisionType
) {
}
