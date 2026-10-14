package fptu.semester8.mss301.listingservice.features.room;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.room.api.dto.CreateRoomRequest;
import fptu.semester8.mss301.listingservice.features.room.api.dto.RoomDetailResponse;
import fptu.semester8.mss301.listingservice.features.room.domain.Room;
import fptu.semester8.mss301.listingservice.features.room.mapper.RoomMapper;
import fptu.semester8.mss301.listingservice.features.room.repository.RoomRepository;
import fptu.semester8.mss301.listingservice.features.room.application.RoomService;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomServiceTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final RoomRepository roomRepository = mock(RoomRepository.class);
    private final BuildingRepository buildingRepository = mock(BuildingRepository.class);
    private final RoomMapper mapper = Mappers.getMapper(RoomMapper.class);
    private final RoomService service = new RoomService(roomRepository, buildingRepository, mapper);

    @Test
    void createValidatesBuildingAndPersistsRoom() {
        Building building = new Building();
        building.setId(12L);
        when(buildingRepository.findByIdAndDeletedAtIsNull(12L)).thenReturn(Optional.of(building));
        when(roomRepository.existsByBuilding_IdAndRoomNumber(12L, "A-01")).thenReturn(false);
        when(roomRepository.saveAndFlush(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RoomDetailResponse result = service.create(new CreateRoomRequest(12L, " A-01 ", null, null,
            2, new BigDecimal("7500000"), "AVAILABLE"));

        assertEquals(12L, result.buildingId());
        assertEquals("A-01", result.roomNumber());
        assertEquals(2, result.maxCapacity());
        assertEquals("AVAILABLE", result.status());
        verify(roomRepository).saveAndFlush(any(Room.class));
    }

    @Test
    void createRejectsMissingBuilding() {
        when(buildingRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.create(new CreateRoomRequest(99L, "A-01", null, null, 1,
                BigDecimal.ZERO, "AVAILABLE")));

        assertEquals(404, exception.getStatusCode().value());
        verify(roomRepository, never()).saveAndFlush(any(Room.class));
    }

    @Test
    void createRejectsDuplicateRoomNumberWithinBuilding() {
        Building building = new Building();
        building.setId(12L);
        when(buildingRepository.findByIdAndDeletedAtIsNull(12L)).thenReturn(Optional.of(building));
        when(roomRepository.existsByBuilding_IdAndRoomNumber(12L, "A-01")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.create(new CreateRoomRequest(12L, "A-01", null, null, 1,
                BigDecimal.ZERO, "AVAILABLE")));

        assertEquals(409, exception.getStatusCode().value());
        verify(roomRepository, never()).saveAndFlush(any(Room.class));
    }

    @Test
    void requestValidationRejectsInvalidCapacityPriceAndStatus() {
        CreateRoomRequest request = new CreateRoomRequest(12L, "A-01", null, null,
            0, new BigDecimal("-1"), " ");

        var violations = validator.validate(request);

        assertEquals(3, violations.size());
    }
}
