package fptu.semester8.mss301.listingservice.features.listing.api;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import fptu.semester8.mss301.listingservice.features.listing.api.dto.ListingPostImageResponse;
import fptu.semester8.mss301.listingservice.features.listing.application.ListingPostImageService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/listing-posts/{listingPostId}/images")
@RequiredArgsConstructor
public class ListingPostImageController {
    private final ListingPostImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ListingPostImageResponse> upload(
        @PathVariable Long listingPostId,
        @RequestParam("file") MultipartFile file,
        @RequestParam @Min(0) Integer sortOrder,
        @RequestParam @NotNull Boolean isCover
    ) {
        return ApiResponse.success(imageService.upload(listingPostId, file, sortOrder, isCover));
    }

    @PutMapping("/{imageId}/cover")
    public ApiResponse<ListingPostImageResponse> setCover(
        @PathVariable Long listingPostId,
        @PathVariable Long imageId
    ) {
        return ApiResponse.success(imageService.setCover(listingPostId, imageId));
    }
}
