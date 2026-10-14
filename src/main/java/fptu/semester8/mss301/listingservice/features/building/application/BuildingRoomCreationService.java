package fptu.semester8.mss301.listingservice.features.building.application;

import fptu.semester8.mss301.listingservice.common.id.SnowflakeIdGenerator;
import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingWithRoomsResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingWithRoomsRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.mapper.BuildingMapper;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.room.mapper.RoomMapper;
import fptu.semester8.mss301.listingservice.features.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BuildingRoomCreationService {

    BuildingRepository buildingRepository;

    RoomRepository roomRepository;

    BuildingMapper buildingMapper;

    RoomMapper roomMapper;

    @Transactional
    public BuildingWithRoomsResponse create(CreateBuildingWithRoomsRequest request) {
        long now = System.currentTimeMillis();
        Building building = buildingMapper.toEntity(request, now);
        building.setId(SnowflakeIdGenerator.nextId());
        Building savedBuilding = buildingRepository.saveAndFlush(building);

        var rooms = request.rooms().stream()
            .map(roomRequest -> roomMapper.toEntity(roomRequest, savedBuilding, now))
            .peek(room -> room.setId(SnowflakeIdGenerator.nextId()))
            .toList();
        try {
            var roomResponses = roomRepository.saveAllAndFlush(rooms).stream()
                .map(roomMapper::toDetailResponse)
                .toList();
            return buildingMapper.toWithRoomsResponse(savedBuilding, roomResponses);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Rooms conflict with an existing record", exception);
        }
    }

}
