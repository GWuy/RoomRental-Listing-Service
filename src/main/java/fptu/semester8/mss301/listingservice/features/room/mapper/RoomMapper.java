package fptu.semester8.mss301.listingservice.features.room.mapper;

import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;
import fptu.semester8.mss301.listingservice.features.room.domain.Room;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRoomRequest;
import fptu.semester8.mss301.listingservice.features.room.api.dto.CreateRoomRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "buildingId", source = "building.id")
    RoomDetailResponse toDetailResponse(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "building", source = "building")
    @Mapping(target = "roomNumber", expression = "java(request.roomNumber().trim())")
    @Mapping(target = "status", expression = "java(request.status().trim())")
    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "updatedAt", source = "now")
    Room toEntity(CreateRoomRequest request, Building building, long now);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "building", source = "building")
    @Mapping(target = "roomNumber", expression = "java(request.roomNumber().trim())")
    @Mapping(target = "status", expression = "java(request.status().trim())")
    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "updatedAt", source = "now")
    Room toEntity(CreateBuildingRoomRequest request, Building building, long now);
}
