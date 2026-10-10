package fptu.semester8.mss301.listingservice.features.building.api;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardDropdownResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardWriteRequest;
import fptu.semester8.mss301.listingservice.features.building.service.WardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** See {@link ProvinceController} for the gateway authorization contract. */
@RestController
@RequestMapping("/api/v1/wards")
public class WardController {
    private final WardService wardService;

    public WardController(WardService wardService) {
        this.wardService = wardService;
    }

    @GetMapping
    public ApiResponse<List<WardDropdownResponse>> list(
        @RequestParam(required = false) String provinceId
    ) {
        return ApiResponse.success(wardService.list(AddressApiUuid.parseOptional(provinceId, "provinceId")));
    }

    @GetMapping("/{id}")
    public ApiResponse<WardDetailResponse> get(@PathVariable String id) {
        return ApiResponse.success(wardService.get(AddressApiUuid.parse(id, "ward id")));
    }

    @PostMapping
    public ApiResponse<WardDetailResponse> create(
        @Valid @RequestBody WardWriteRequest request
    ) {
        return ApiResponse.success(wardService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<WardDetailResponse> update(
        @PathVariable String id,
        @Valid @RequestBody WardWriteRequest request
    ) {
        return ApiResponse.success(wardService.update(AddressApiUuid.parse(id, "ward id"), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        wardService.delete(AddressApiUuid.parse(id, "ward id"));
        return ApiResponse.success(null);
    }
}
