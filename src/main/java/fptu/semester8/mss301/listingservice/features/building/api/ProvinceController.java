package fptu.semester8.mss301.listingservice.features.building.api;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceDropdownResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceWriteRequest;
import fptu.semester8.mss301.listingservice.features.building.service.ProvinceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Write routes rely on the API gateway to authenticate administrators and strip
 * client-supplied identity/role headers before forwarding the request.
 */
@RestController
@RequestMapping("/api/v1/provinces")
public class ProvinceController {
    private final ProvinceService provinceService;

    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @GetMapping
    public ApiResponse<List<ProvinceDropdownResponse>> list() {
        return ApiResponse.success(provinceService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProvinceDetailResponse> get(@PathVariable String id) {
        return ApiResponse.success(provinceService.get(AddressApiUuid.parse(id, "province id")));
    }

    @PostMapping
    public ApiResponse<ProvinceDetailResponse> create(
        @Valid @RequestBody ProvinceWriteRequest request
    ) {
        return ApiResponse.success(provinceService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProvinceDetailResponse> update(
        @PathVariable String id,
        @Valid @RequestBody ProvinceWriteRequest request
    ) {
        return ApiResponse.success(provinceService.update(AddressApiUuid.parse(id, "province id"), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        provinceService.delete(AddressApiUuid.parse(id, "province id"));
        return ApiResponse.success(null);
    }
}
