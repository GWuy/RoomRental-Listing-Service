package fptu.semester8.mss301.listingservice.features.building;

import fptu.semester8.mss301.listingservice.features.building.api.dto.BuildingPageableRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildingPageableRequestTest {
    @Test
    void defaultsToFirstPageTenItems() {
        var pageable = new BuildingPageableRequest(null, null).toPageable();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(org.springframework.data.domain.Sort.unsorted(), pageable.getSort());
    }

    @Test
    void rejectsInvalidPageAndPageSizes() {
        assertBadRequest(new BuildingPageableRequest(-1, 10));
        assertBadRequest(new BuildingPageableRequest(0, 0));
        assertBadRequest(new BuildingPageableRequest(0, -2));
        assertBadRequest(new BuildingPageableRequest(0, 101));
    }

    private void assertBadRequest(BuildingPageableRequest request) {
        var exception = org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class, request::toPageable);
        assertEquals(400, exception.getStatusCode().value());
    }
}
