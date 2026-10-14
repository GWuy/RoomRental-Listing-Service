package fptu.semester8.mss301.listingservice.features.listing;

import fptu.semester8.mss301.listingservice.features.building.domain.Building;
import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPost;
import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPostImage;
import fptu.semester8.mss301.listingservice.features.listing.domain.RoomListing;
import fptu.semester8.mss301.listingservice.features.listing.repository.ListingPostImageRepository;
import fptu.semester8.mss301.listingservice.features.listing.repository.ListingPostRepository;
import fptu.semester8.mss301.listingservice.features.listing.repository.RoomListingRepository;
import fptu.semester8.mss301.listingservice.features.room.domain.Room;
import fptu.semester8.mss301.listingservice.features.building.repository.BuildingRepository;
import fptu.semester8.mss301.listingservice.features.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class ListingDomainPostgresIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
        .withInitScript("db/test/create-listing-schema.sql");

    @Autowired private BuildingRepository buildingRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RoomListingRepository roomListingRepository;
    @Autowired private ListingPostRepository listingPostRepository;
    @Autowired private ListingPostImageRepository imageRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Room room;
    private ListingPost post;

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl() + "?currentSchema=listing_booking_service");
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uq_room_listing_room_id " +
            "ON listing_booking_service.room_listing(room_id)");
        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uq_listing_post_image_cover " +
            "ON listing_booking_service.listing_post_image(listing_post_id) WHERE is_cover = TRUE");

        Building building = new Building();
        building.setId(100L);
        building.setName("Building");
        building.setAddress("Address");
        building.setStatus("ACTIVE");
        building.setCreatedAt(1L);
        building.setUpdatedAt(1L);
        buildingRepository.saveAndFlush(building);

        room = new Room();
        room.setId(200L);
        room.setBuilding(building);
        room.setRoomNumber("101");
        room.setMaxCapacity(2);
        room.setPrice(BigDecimal.valueOf(100));
        room.setStatus("ACTIVE");
        room.setCreatedAt(1L);
        room.setUpdatedAt(1L);
        roomRepository.saveAndFlush(room);

        post = savePost(300L, "ARCHIVED");
    }

    @Test
    void roomMayHaveOnlyOneRoomListingForItsLifetime() {
        roomListingRepository.saveAndFlush(roomListing(400L));

        assertThrows(DataIntegrityViolationException.class,
            () -> roomListingRepository.saveAndFlush(roomListing(401L)));
    }

    @Test
    void listingPostCanHaveMultipleImages() {
        imageRepository.saveAndFlush(image(500L, post, false));
        imageRepository.saveAndFlush(image(501L, post, false));

        assertEquals(2, imageRepository.findAllByListingPost_Id(post.getId()).size());
    }

    @Test
    void listingPostCanHaveAtMostOneCoverImage() {
        imageRepository.saveAndFlush(image(510L, post, true));

        assertThrows(DataIntegrityViolationException.class,
            () -> imageRepository.saveAndFlush(image(511L, post, true)));
    }

    @Test
    void aRoomMayHaveMultipleListingPostsAcrossItsHistory() {
        savePost(301L, "ARCHIVED");

        assertEquals(2, listingPostRepository.count());
    }

    private ListingPost savePost(Long id, String status) {
        ListingPost listingPost = new ListingPost();
        listingPost.setId(id);
        listingPost.setRoom(room);
        listingPost.setTitle("Listing " + id);
        listingPost.setStatus(status);
        listingPost.setCreatedAt(1L);
        listingPost.setUpdatedAt(1L);
        return listingPostRepository.saveAndFlush(listingPost);
    }

    private RoomListing roomListing(Long id) {
        RoomListing listing = new RoomListing();
        listing.setId(id);
        listing.setRoomId(room.getId());
        listing.setBuildingId(room.getBuilding().getId());
        listing.setBuildingName("Building");
        listing.setAddress("Address");
        listing.setRoomNumber(room.getRoomNumber());
        listing.setMaxCapacity(2);
        listing.setPrice(BigDecimal.valueOf(100));
        listing.setRoomStatus("ACTIVE");
        listing.setTitle("Room listing " + id);
        listing.setStatus("ACTIVE");
        listing.setCreatedAt(1L);
        listing.setUpdatedAt(1L);
        return listing;
    }

    private ListingPostImage image(Long id, ListingPost listingPost, boolean cover) {
        ListingPostImage image = new ListingPostImage();
        image.setId(id);
        image.setListingPost(listingPost);
        image.setObjectKey("listings/" + listingPost.getId() + "/" + id + ".jpg");
        image.setSortOrder(0);
        image.setIsCover(cover);
        image.setCreatedAt(1L);
        return image;
    }
}
