package fptu.semester8.mss301.listingservice.features.building.api;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRequest;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingWithRoomsRequest;
import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingWithRoomsResponse;
import fptu.semester8.mss301.listingservice.features.building.application.BuildingService;
import fptu.semester8.mss301.listingservice.features.building.application.BuildingRoomCreationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BuildingController {

    BuildingService buildingService;

    @PostMapping
    public ApiResponse<BuildingDetailResponse> create(@Valid @RequestBody CreateBuildingRequest request) {
        return ApiResponse.success(buildingService.createBuilding(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<BuildingDetailResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(buildingService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<BuildingDetailResponse>> list(Pageable pageable) {
        return ApiResponse.success(buildingService.list(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<BuildingDetailResponse>> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String address,
        Pageable pageable
    ) {
        return ApiResponse.success(buildingService.search(name, address, pageable));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<BuildingDetailResponse> softDelete(@PathVariable Long id) {
        return ApiResponse.success(buildingService.softDelete(id));
    }
}
