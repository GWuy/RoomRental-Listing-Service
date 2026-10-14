package fptu.semester8.mss301.listingservice.features.building;

import fptu.semester8.mss301.listingservice.features.building.application.BuildingService;
import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingDetailResponse;
import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.building.mapper.BuildingMapper;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class BuildingSearchPostgresIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
        .withInitScript("db/test/create-listing-schema.sql");

    @Autowired
    private BuildingRepository repository;

    private BuildingService service;

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl() + "?currentSchema=listing_booking_service");
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @BeforeEach
    void setUp() {
        service = new BuildingService(repository, Mappers.getMapper(BuildingMapper.class));
    }

    @Test
    void searchesCaseInsensitivePartialNameOnly() {
        save(1L, "Sunrise Apartment", "District 1", 300L, null);
        save(2L, "Moonlight Home", "District 2", 200L, null);

        Page<BuildingDetailResponse> results = service.search("SUNRI", null, PageRequest.of(0, 10));

        assertEquals(List.of(1L), ids(results));
    }

    @Test
    void searchesAddressOnly() {
        save(3L, "Home", "Nguyen Hue Street", 300L, null);
        save(4L, "Office", "Le Loi Street", 200L, null);

        Page<BuildingDetailResponse> results = service.search(null, "NGUYEN", PageRequest.of(0, 10));

        assertEquals(List.of(3L), ids(results));
    }

    @Test
    void searchesByBothNameAndAddress() {
        save(5L, "Sunrise Apartment", "Nguyen Hue", 300L, null);
        save(6L, "Sunrise Apartment", "Le Loi", 200L, null);
        save(7L, "Moonlight Home", "Nguyen Hue", 100L, null);

        Page<BuildingDetailResponse> results = service.search("sunrise", "hue", PageRequest.of(0, 10));

        assertEquals(List.of(5L), ids(results));
    }

    @Test
    void omittedNullAndBlankCriteriaReturnAllNonDeletedRows() {
        save(8L, "Alpha", "One", 300L, null);
        save(9L, "Beta", "Two", 200L, null);
        save(10L, "Hidden", "Three", 100L, 123L);

        assertEquals(List.of(8L, 9L), ids(service.search(null, null, PageRequest.of(0, 10))));
        assertEquals(List.of(8L, 9L), ids(service.search("  ", "", PageRequest.of(0, 10))));
    }

    @Test
    void appliesPaginationAndCreatedAtDescendingOrder() {
        save(11L, "Alpha", "One", 100L, null);
        save(12L, "Beta", "Two", 300L, null);
        save(13L, "Gamma", "Three", 200L, null);

        Page<BuildingDetailResponse> firstPage = service.search(null, null, PageRequest.of(0, 2));
        Page<BuildingDetailResponse> secondPage = service.search(null, null, PageRequest.of(1, 2));

        assertEquals(List.of(12L, 13L), ids(firstPage));
        assertEquals(List.of(11L), ids(secondPage));
        assertEquals(3, firstPage.getTotalElements());
    }

    private void save(long id, String name, String address, long createdAt, Long deletedAt) {
        Building building = new Building();
        building.setId(id);
        building.setName(name);
        building.setAddress(address);
        building.setStatus("ACTIVE");
        building.setCreatedAt(createdAt);
        building.setUpdatedAt(createdAt);
        building.setDeletedAt(deletedAt);
        repository.saveAndFlush(building);
    }

    private List<Long> ids(Page<BuildingDetailResponse> page) {
        return page.getContent().stream().map(BuildingDetailResponse::id).toList();
    }
}
