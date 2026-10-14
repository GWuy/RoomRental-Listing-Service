package fptu.semester8.mss301.listingservice.features.building;

import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.mapper.BuildingMapper;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.building.application.BuildingService;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.server.ResponseStatusException;

class BuildingServiceTest {
    private final BuildingRepository repository = mock(BuildingRepository.class);
    private final BuildingMapper mapper = Mappers.getMapper(BuildingMapper.class);
    private final BuildingService service = new BuildingService(repository, mapper);

    @Test
    void createBuildingTrimsFieldsAndSetsActiveStatus() {
        when(repository.saveAndFlush(any(Building.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BuildingDetailResponse result = service.createBuilding(new CreateBuildingRequest("  House  ", "  Address  ", null, null));

        assertEquals("House", result.name());
        assertEquals("Address", result.address());
        assertEquals("ACTIVE", result.status());
        verify(repository).saveAndFlush(any(Building.class));
    }

    @Test
    void getByIdReturnsNotFoundWhenBuildingDoesNotExist() {
        when(repository.findByIdAndDeletedAtIsNull(44L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.getById(44L));
        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void softDeleteSetsDeletionTimestampAndReturnsBuilding() {
        Building building = new Building();
        building.setId(44L);
        building.setName("Home");
        building.setAddress("Street");
        building.setStatus("ACTIVE");
        when(repository.findByIdAndDeletedAtIsNull(44L)).thenReturn(Optional.of(building));
        when(repository.saveAndFlush(building)).thenReturn(building);
        service.softDelete(44L);

        org.junit.jupiter.api.Assertions.assertNotNull(building.getDeletedAt());
        assertEquals(building.getDeletedAt(), building.getUpdatedAt());
        verify(repository).saveAndFlush(building);
    }

    @Test
    void listPreservesRepositorySoftDeleteFilterAndCreatedAtDefaultOrder() {
        when(repository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(any())).thenReturn(Page.empty());
        service.list(PageRequest.of(0, 10));

        var pageable = org.mockito.ArgumentCaptor.forClass(org.springframework.data.domain.Pageable.class);
        verify(repository).findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable.capture());
        assertFalse(pageable.getValue().getSort().isSorted());
    }

}
