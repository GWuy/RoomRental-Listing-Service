package fptu.semester8.mss301.listingservice.features.building.service;

import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.api.dto.CreateBuildingRequest;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.common.id.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class BuildingService {
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Transactional
    public BuildingDetailResponse create(CreateBuildingRequest request) {
        long now = System.currentTimeMillis();
        Building building = new Building();
        building.setId(SnowflakeIdGenerator.nextId());
        building.setName(request.name().trim());
        building.setAddress(request.address().trim());
        building.setStatus("ACTIVE");
        building.setLatitude(request.latitude());
        building.setLongitude(request.longitude());
        building.setCreatedAt(now);
        building.setUpdatedAt(now);

        return toDetail(buildingRepository.saveAndFlush(building));
    }

    private BuildingDetailResponse toDetail(Building building) {
        return new BuildingDetailResponse(
            building.getId(), building.getName(), building.getAddress(), building.getStatus(),
            building.getLatitude(), building.getLongitude(),
            building.getCreatedAt(), building.getUpdatedAt()
        );
    }
}
