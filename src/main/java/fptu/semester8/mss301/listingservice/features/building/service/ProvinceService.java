package fptu.semester8.mss301.listingservice.features.building.service;

import fptu.exe202.signify.apiresponse.exception.ConflictException;
import fptu.exe202.signify.apiresponse.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceDropdownResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceWriteRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Province;
import fptu.semester8.mss301.listingservice.features.building.repository.ProvinceRepository;
import fptu.semester8.mss301.listingservice.features.building.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    public ProvinceService(ProvinceRepository provinceRepository, WardRepository wardRepository) {
        this.provinceRepository = provinceRepository;
        this.wardRepository = wardRepository;
    }

    public List<ProvinceDropdownResponse> list() {
        return provinceRepository.findAllByOrderByNameAsc().stream()
            .map(province -> new ProvinceDropdownResponse(province.getId(), province.getCode(), province.getName()))
            .toList();
    }

    public ProvinceDetailResponse get(UUID id) {
        return toDetail(findProvince(id));
    }

    @Transactional
    public ProvinceDetailResponse create(ProvinceWriteRequest request) {
        if (provinceRepository.existsByCode(request.code())) {
            throw new ConflictException("Province code already exists: " + request.code());
        }
        long now = System.currentTimeMillis();
        Province province = new Province();
        province.setId(UUID.randomUUID());
        apply(province, request);
        province.setCreatedAt(now);
        province.setUpdatedAt(now);
        return toDetail(save(province));
    }

    @Transactional
    public ProvinceDetailResponse update(UUID id, ProvinceWriteRequest request) {
        Province province = findProvince(id);
        if (provinceRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new ConflictException("Province code already exists: " + request.code());
        }
        apply(province, request);
        province.setUpdatedAt(System.currentTimeMillis());
        return toDetail(save(province));
    }

    @Transactional
    public void delete(UUID id) {
        Province province = findProvince(id);
        if (wardRepository.existsByProvinceId(id)) {
            throw new ConflictException("Province cannot be deleted while wards reference it.");
        }
        try {
            provinceRepository.delete(province);
            provinceRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Province cannot be deleted while address data references it.");
        }
    }

    private Province findProvince(UUID id) {
        return provinceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Province not found: " + id));
    }

    private void apply(Province province, ProvinceWriteRequest request) {
        province.setCode(request.code());
        province.setName(request.name().trim());
        province.setCodename(request.codename());
        province.setDivisionType(request.divisionType());
    }

    private Province save(Province province) {
        try {
            return provinceRepository.saveAndFlush(province);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Province code conflicts with existing address data.");
        }
    }

    private ProvinceDetailResponse toDetail(Province province) {
        return new ProvinceDetailResponse(
            province.getId(), province.getCode(), province.getName(), province.getCodename(),
            province.getDivisionType(), province.getCreatedAt(), province.getUpdatedAt()
        );
    }
}
