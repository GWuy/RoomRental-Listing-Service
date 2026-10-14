package fptu.semester8.mss301.listingservice.features.building.application;

import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.common.id.SnowflakeIdGenerator;
import fptu.semester8.mss301.listingservice.features.building.mapper.BuildingMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BuildingService {

    BuildingRepository buildingRepository;

    BuildingMapper buildingMapper;

    @Transactional
    public BuildingDetailResponse createBuilding(CreateBuildingRequest request) {
        long now = System.currentTimeMillis();
        var building = buildingMapper.toEntity(request, now);
        building.setId(SnowflakeIdGenerator.nextId());

        return buildingMapper.toDetailResponse(buildingRepository.saveAndFlush(building));
    }

    public BuildingDetailResponse getById(Long id) {
        return buildingRepository.findByIdAndDeletedAtIsNull(id)
            .map(buildingMapper::toDetailResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
    }

    public Page<BuildingDetailResponse> list(Pageable pageable) {
        return buildingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)
            .map(buildingMapper::toDetailResponse);
    }

    public Page<BuildingDetailResponse> search(String name, String address, Pageable pageable) {
        name = normalize(name);
        address = normalize(address);
        if (name == null && address == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide a name or address search term");
        }
        return buildingRepository.search(name, address, pageable).map(buildingMapper::toDetailResponse);
    }

    @Transactional
    public BuildingDetailResponse softDelete(Long id) {
        Building building = buildingRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
        building.setDeletedAt(System.currentTimeMillis());
        building.setUpdatedAt(building.getDeletedAt());
        return buildingMapper.toDetailResponse(buildingRepository.saveAndFlush(building));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
