package fptu.semester8.mss301.listingservice.features.room.application;

import fptu.semester8.mss301.listingservice.common.id.SnowflakeIdGenerator;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.room.api.dto.CreateRoomRequest;
import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;
import fptu.semester8.mss301.listingservice.features.room.mapper.RoomMapper;
import fptu.semester8.mss301.listingservice.features.room.repository.RoomRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, BuildingRepository buildingRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
        this.roomMapper = roomMapper;
    }

    @Transactional
    public RoomDetailResponse create(CreateRoomRequest request) {
        Building building = buildingRepository.findByIdAndDeletedAtIsNull(request.buildingId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
        if (roomRepository.existsByBuilding_IdAndRoomNumber(request.buildingId(), request.roomNumber().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room number already exists in this building");
        }

        long now = System.currentTimeMillis();
        var room = roomMapper.toEntity(request, building, now);
        room.setId(SnowflakeIdGenerator.nextId());
        try {
            return roomMapper.toDetailResponse(roomRepository.saveAndFlush(room));
        } catch (DataIntegrityViolationException exception) {
            // The database unique constraint is authoritative when concurrent creates race.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room conflicts with an existing record", exception);
        }
    }

    public RoomDetailResponse getById(Long id) {
        return roomRepository.findById(id)
            .filter(room -> room.getBuilding().getDeletedAt() == null)
            .map(roomMapper::toDetailResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    public Page<RoomDetailResponse> list(Pageable pageable) {
        return roomRepository.findAllByBuilding_DeletedAtIsNullOrderByCreatedAtDesc(pageable)
            .map(roomMapper::toDetailResponse);
    }

    public Page<RoomDetailResponse> search(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide a room search term");
        }
        return roomRepository.search(keyword.trim(), pageable).map(roomMapper::toDetailResponse);
    }
}
