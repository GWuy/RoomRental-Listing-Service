package fptu.semester8.mss301.listingservice.features.storage.api.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteImageRequest(@NotBlank String key) {
}
