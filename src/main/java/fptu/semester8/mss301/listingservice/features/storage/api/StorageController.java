package fptu.semester8.mss301.listingservice.features.storage.api;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import fptu.semester8.mss301.listingservice.features.storage.api.dto.DeleteImageRequest;
import fptu.semester8.mss301.listingservice.features.storage.api.dto.PresignedUrlResponse;
import fptu.semester8.mss301.listingservice.features.storage.application.StorageService;
import fptu.semester8.mss301.listingservice.features.storage.mapper.StorageMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/storage/images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageController {

    StorageService storageService;
    StorageMapper storageMapper;

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Void> delete(@Valid @RequestBody DeleteImageRequest request) {
        storageService.deleteImage(request.key());
        return ApiResponse.success("Image deleted successfully", null);
    }

    @GetMapping("/url")
    public ApiResponse<PresignedUrlResponse> url(@RequestParam("key") String key) {
        return ApiResponse.success(storageMapper.toPresignedUrlResponse(storageService.generateUrl(key)));
    }
}
