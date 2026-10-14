package fptu.semester8.mss301.listingservice.features.building.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRequest;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingWithRoomsRequest;
import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingWithRoomsResponse;
import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BuildingMapper {
    BuildingDetailResponse toDetailResponse(Building building);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(request.name().trim())")
    @Mapping(target = "address", expression = "java(request.address().trim())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "updatedAt", source = "now")
    @Mapping(target = "deletedAt", ignore = true)
    Building toEntity(CreateBuildingRequest request, long now);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(request.name().trim())")
    @Mapping(target = "address", expression = "java(request.address().trim())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "updatedAt", source = "now")
    @Mapping(target = "deletedAt", ignore = true)
    Building toEntity(CreateBuildingWithRoomsRequest request, long now);

    @Mapping(target = "building", source = "building")
    @Mapping(target = "rooms", source = "rooms")
    BuildingWithRoomsResponse toWithRoomsResponse(Building building, List<RoomDetailResponse> rooms);
}
