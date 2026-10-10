package fptu.semester8.mss301.listingservice.features.building.service;

import fptu.exe202.signify.apiresponse.exception.ConflictException;
import fptu.exe202.signify.apiresponse.exception.ResourceNotFoundException;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceWriteRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Province;
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

class ProvinceServiceTest {
    private ProvinceRepository provinceRepository;
    private WardRepository wardRepository;
    private ProvinceService service;

    @BeforeEach
    void setUp() {
        provinceRepository = mock(ProvinceRepository.class);
        wardRepository = mock(WardRepository.class);
        service = new ProvinceService(provinceRepository, wardRepository);
    }

    @Test
    void listReturnsOnlyDropdownFieldsInRepositoryOrder() {
        Province province = province(UUID.randomUUID(), 79, "Thành phố Hồ Chí Minh");
        when(provinceRepository.findAllByOrderByNameAsc()).thenReturn(List.of(province));

        var result = service.list();

        assertEquals(1, result.size());
        assertEquals(province.getId(), result.getFirst().id());
        assertEquals(79, result.getFirst().code());
        assertEquals("Thành phố Hồ Chí Minh", result.getFirst().name());
    }

    @Test
    void getReturnsNotFoundWhenProvinceDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(provinceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }

    @Test
    void createGeneratesIdAndTimestamps() {
        when(provinceRepository.existsByCode(79)).thenReturn(false);
        when(provinceRepository.saveAndFlush(any(Province.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.create(new ProvinceWriteRequest(79, " Thành phố Hồ Chí Minh ", "ho_chi_minh", "thành phố"));

        assertNotNull(result.id());
        assertEquals(79, result.code());
        assertEquals("Thành phố Hồ Chí Minh", result.name());
        assertNotNull(result.createdAt());
        assertEquals(result.createdAt(), result.updatedAt());
    }

    @Test
    void createRejectsDuplicateCode() {
        when(provinceRepository.existsByCode(79)).thenReturn(true);

        assertThrows(ConflictException.class,
            () -> service.create(new ProvinceWriteRequest(79, "Hồ Chí Minh", null, null)));
        verify(provinceRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteRejectsProvinceWithWards() {
        UUID id = UUID.randomUUID();
        Province province = province(id, 79, "Hồ Chí Minh");
        when(provinceRepository.findById(id)).thenReturn(Optional.of(province));
        when(wardRepository.existsByProvinceId(id)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.delete(id));
        verify(provinceRepository, never()).delete(any(Province.class));
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
