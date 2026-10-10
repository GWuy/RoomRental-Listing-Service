package fptu.semester8.mss301.listingservice.features.building.service;

import fptu.exe202.signify.apiresponse.exception.ConflictException;
import fptu.exe202.signify.apiresponse.exception.ResourceNotFoundException;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardWriteRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Province;
import fptu.semester8.mss301.listingservice.features.building.domain.Ward;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.building.repository.ProvinceRepository;
import fptu.semester8.mss301.listingservice.features.building.repository.WardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WardServiceTest {
    private WardRepository wardRepository;
    private ProvinceRepository provinceRepository;
    private BuildingRepository buildingRepository;
    private WardService service;

    @BeforeEach
    void setUp() {
        wardRepository = mock(WardRepository.class);
        provinceRepository = mock(ProvinceRepository.class);
        buildingRepository = mock(BuildingRepository.class);
        service = new WardService(wardRepository, provinceRepository, buildingRepository);
    }

    @Test
    void listFiltersByProvinceInRepository() {
        UUID provinceId = UUID.randomUUID();
        Ward ward = ward(UUID.randomUUID(), provinceId, 26560, "Phường Bà Rịa");
        when(wardRepository.findAllByProvinceIdOrderByNameAsc(provinceId)).thenReturn(List.of(ward));

        var result = service.list(provinceId);

        assertEquals(1, result.size());
        assertEquals(provinceId, result.getFirst().provinceId());
        verify(wardRepository).findAllByProvinceIdOrderByNameAsc(provinceId);
        verify(wardRepository, never()).findAllByOrderByNameAsc();
    }

    @Test
    void listWithoutProvinceUsesAllWardQuery() {
        when(wardRepository.findAllByOrderByNameAsc()).thenReturn(List.of());

        assertTrue(service.list(null).isEmpty());

        verify(wardRepository).findAllByOrderByNameAsc();
        verify(wardRepository, never()).findAllByProvinceIdOrderByNameAsc(any());
    }

    @Test
    void listRejectsUnknownProvinceId() {
        UUID provinceId = UUID.randomUUID();
        when(provinceRepository.existsById(provinceId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.list(provinceId));
        verify(wardRepository, never()).findAllByProvinceIdOrderByNameAsc(any());
    }

    @Test
    void getReturnsWardWithProvinceSummary() {
        UUID provinceId = UUID.randomUUID();
        UUID wardId = UUID.randomUUID();
        Ward ward = ward(wardId, provinceId, 26560, "Phường Bà Rịa");
        Province province = province(provinceId, 79, "Thành phố Hồ Chí Minh");
        when(wardRepository.findById(wardId)).thenReturn(Optional.of(ward));
        when(provinceRepository.findById(provinceId)).thenReturn(Optional.of(province));

        var result = service.get(wardId);

        assertEquals(wardId, result.id());
        assertEquals(provinceId, result.province().id());
        assertEquals(79, result.province().code());
    }

    @Test
    void createRequiresExistingProvinceAndCreatesInternalId() {
        UUID provinceId = UUID.randomUUID();
        Province province = province(provinceId, 79, "Thành phố Hồ Chí Minh");
        when(provinceRepository.findById(provinceId)).thenReturn(Optional.of(province));
        when(wardRepository.existsByCode(26560)).thenReturn(false);
        when(wardRepository.saveAndFlush(any(Ward.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.create(new WardWriteRequest(provinceId, 26560, "Phường Bà Rịa", "phuong_ba_ria", "phường"));

        assertNotNull(result.id());
        assertEquals(provinceId, result.province().id());
        assertNotNull(result.createdAt());
        assertEquals(result.createdAt(), result.updatedAt());
    }

    @Test
    void createRejectsUnknownProvinceAndDuplicateCode() {
        UUID missingProvinceId = UUID.randomUUID();
        when(provinceRepository.findById(missingProvinceId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.create(
            new WardWriteRequest(missingProvinceId, 26560, "Bà Rịa", null, null)));

        UUID provinceId = UUID.randomUUID();
        when(provinceRepository.findById(provinceId)).thenReturn(Optional.of(province(provinceId, 79, "Hồ Chí Minh")));
        when(wardRepository.existsByCode(26560)).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.create(
            new WardWriteRequest(provinceId, 26560, "Bà Rịa", null, null)));
        verify(wardRepository, never()).saveAndFlush(any(Ward.class));
    }

    @Test
    void deleteRejectsWardUsedByBuilding() {
        UUID wardId = UUID.randomUUID();
        when(wardRepository.findById(wardId)).thenReturn(Optional.of(ward(wardId, UUID.randomUUID(), 26560, "Bà Rịa")));
        when(buildingRepository.existsByWardId(wardId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.delete(wardId));
        verify(wardRepository, never()).delete(any(Ward.class));
    }

    private Ward ward(UUID id, UUID provinceId, int code, String name) {
        Ward ward = new Ward();
        ward.setId(id);
        ward.setProvinceId(provinceId);
        ward.setCode(code);
        ward.setName(name);
        ward.setCreatedAt(1L);
        ward.setUpdatedAt(1L);
        return ward;
    }

    private Province province(UUID id, int code, String name) {
        Province province = new Province();
        province.setId(id);
        province.setCode(code);
        province.setName(name);
        province.setCreatedAt(1L);
        province.setUpdatedAt(1L);
        return province;
    }
}
