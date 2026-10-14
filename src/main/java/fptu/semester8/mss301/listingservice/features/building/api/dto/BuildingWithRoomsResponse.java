package fptu.semester8.mss301.listingservice.features.building.api.dto;

import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;

import java.util.List;

public record BuildingWithRoomsResponse(BuildingDetailResponse building, List<RoomDetailResponse> rooms) {
}
