package fptu.semester8.mss301.listingservice.features.building;

import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRoomRequest;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingWithRoomsRequest;
import fptu.semester8.mss301.listingservice.features.building.application.BuildingRoomCreationService;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.mapper.BuildingMapper;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;
import fptu.semester8.mss301.listingservice.features.room.domain.Room;
import fptu.semester8.mss301.listingservice.features.room.mapper.RoomMapper;
import fptu.semester8.mss301.listingservice.features.room.repository.RoomRepository;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildingRoomCreationServiceTest {
    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final BuildingMapper buildingMapper = Mappers.getMapper(BuildingMapper.class);
    private final RoomMapper roomMapper = Mappers.getMapper(RoomMapper.class);
    private final BuildingRoomCreationService service = new BuildingRoomCreationService(
        buildingRepository, roomRepository, buildingMapper, roomMapper);

    @Test
    void createsOneBuildingAndAllRequestedRoomsTogether() {
        when(buildingRepository.saveAndFlush(any(Building.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.create(new CreateBuildingWithRoomsRequest("Building", "Address", null, null,
            List.of(room("A-01"), room("A-02"))));

        assertEquals(2, response.rooms().size());
        assertEquals(response.rooms().get(0).buildingId(), response.rooms().get(1).buildingId());
        assertEquals("A-01", response.rooms().get(0).roomNumber());
    }

    private CreateBuildingRoomRequest room(String number) {
        return new CreateBuildingRoomRequest(number, null, null, 2, new BigDecimal("1000000"), "AVAILABLE");
    }
}
