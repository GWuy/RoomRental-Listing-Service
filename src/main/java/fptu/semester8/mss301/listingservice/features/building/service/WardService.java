package fptu.semester8.mss301.listingservice.features.building.service;

import fptu.exe202.signify.apiresponse.exception.ConflictException;
import fptu.exe202.signify.apiresponse.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import fptu.semester8.mss301.listingservice.features.building.api.dto.ProvinceSummaryResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardDropdownResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.WardWriteRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Province;
import fptu.semester8.mss301.listingservice.features.building.domain.Ward;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.building.repository.ProvinceRepository;
import fptu.semester8.mss301.listingservice.features.building.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WardService {
    private final WardRepository wardRepository;
    private final ProvinceRepository provinceRepository;
    private final BuildingRepository buildingRepository;

    public WardService(WardRepository wardRepository, ProvinceRepository provinceRepository,
                       BuildingRepository buildingRepository) {
        this.wardRepository = wardRepository;
        this.provinceRepository = provinceRepository;
        this.buildingRepository = buildingRepository;
    }

    public List<WardDropdownResponse> list(UUID provinceId) {
        List<Ward> wards;
        if (provinceId == null) {
            wards = wardRepository.findAllByOrderByNameAsc();
        } else {
            if (!provinceRepository.existsById(provinceId)) {
                throw new ResourceNotFoundException("Province not found: " + provinceId);
            }
            wards = wardRepository.findAllByProvinceIdOrderByNameAsc(provinceId);
        }
        return wards.stream()
            .map(ward -> new WardDropdownResponse(ward.getId(), ward.getCode(), ward.getName(), ward.getProvinceId()))
            .toList();
    }

    public WardDetailResponse get(UUID id) {
        Ward ward = findWard(id);
        Province province = provinceRepository.findById(ward.getProvinceId())
            .orElseThrow(() -> new ResourceNotFoundException("Province not found: " + ward.getProvinceId()));
        return toDetail(ward, province);
    }

    @Transactional
    public WardDetailResponse create(WardWriteRequest request) {
        Province province = findProvince(request.provinceId());
        if (wardRepository.existsByCode(request.code())) {
            throw new ConflictException("Ward code already exists: " + request.code());
        }
        long now = System.currentTimeMillis();
        Ward ward = new Ward();
        ward.setId(UUID.randomUUID());
        apply(ward, request);
        ward.setCreatedAt(now);
        ward.setUpdatedAt(now);
        return toDetail(save(ward), province);
    }

    @Transactional
    public WardDetailResponse update(UUID id, WardWriteRequest request) {
        Ward ward = findWard(id);
        Province province = findProvince(request.provinceId());
        if (wardRepository.existsByCodeAndIdNot(request.code(), id)) {
            throw new ConflictException("Ward code already exists: " + request.code());
        }
        apply(ward, request);
        ward.setUpdatedAt(System.currentTimeMillis());
        return toDetail(save(ward), province);
    }

    @Transactional
    public void delete(UUID id) {
        Ward ward = findWard(id);
        if (buildingRepository.existsByWardId(id)) {
            throw new ConflictException("Ward cannot be deleted while buildings reference it.");
        }
        try {
            wardRepository.delete(ward);
            wardRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Ward cannot be deleted while building or address data references it.");
        }
    }

    private Ward findWard(UUID id) {
        return wardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ward not found: " + id));
    }

    private Province findProvince(UUID id) {
        return provinceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Province not found: " + id));
    }

    private void apply(Ward ward, WardWriteRequest request) {
        ward.setProvinceId(request.provinceId());
        ward.setCode(request.code());
        ward.setName(request.name().trim());
        ward.setCodename(request.codename());
        ward.setDivisionType(request.divisionType());
    }

    private Ward save(Ward ward) {
        try {
            return wardRepository.saveAndFlush(ward);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Ward code conflicts with existing address data.");
        }
    }

    private WardDetailResponse toDetail(Ward ward, Province province) {
        ProvinceSummaryResponse provinceResponse = new ProvinceSummaryResponse(
            province.getId(), province.getCode(), province.getName()
        );
        return new WardDetailResponse(
            ward.getId(), ward.getCode(), ward.getName(), ward.getCodename(), ward.getDivisionType(),
            ward.getCreatedAt(), ward.getUpdatedAt(), provinceResponse
        );
    }
}
