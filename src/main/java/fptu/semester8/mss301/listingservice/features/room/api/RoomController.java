package fptu.semester8.mss301.listingservice.features.room.api;

import fptu.exe202.signify.apiresponse.response.ApiResponse;
import fptu.semester8.mss301.listingservice.features.room.api.dto.CreateRoomRequest;
import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;
import fptu.semester8.mss301.listingservice.features.room.application.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

    @PostMapping
    public ApiResponse<RoomDetailResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        return ApiResponse.success(roomService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomDetailResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(roomService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<RoomDetailResponse>> list(Pageable pageable) {
        return ApiResponse.success(roomService.list(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<RoomDetailResponse>> search(@RequestParam String keyword, Pageable pageable) {
        return ApiResponse.success(roomService.search(keyword, pageable));
    }
}
