package fptu.semester8.mss301.listingservice.features.building;

import fptu.semester8.mss301.listingservice.features.building.api.BuildingController;
import fptu.semester8.mss301.listingservice.features.building.application.BuildingService;
import fptu.semester8.mss301.listingservice.features.building.application.BuildingRoomCreationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuildingControllerTest {
    private final BuildingService service = mock(BuildingService.class);
    private final BuildingRoomCreationService creationService = mock(BuildingRoomCreationService.class);
    private final BuildingController controller = new BuildingController(service, creationService);

    @Test
    void listDefaultsToFirstPageTenItemsWithoutSortParameter() {
        when(service.list(any())).thenReturn(Page.empty());
        controller.list(0, 10);
        verify(service).list(PageRequest.of(0, 10));
    }

    @Test
    void listRejectsInvalidPagination() {
        for (int page : new int[] {-1}) {
            assertEquals(HttpStatus.BAD_REQUEST,
                assertThrows(ResponseStatusException.class, () -> controller.list(page, 10)).getStatusCode());
        }
        for (int size : new int[] {0, -1, 101}) {
            assertEquals(HttpStatus.BAD_REQUEST,
                assertThrows(ResponseStatusException.class, () -> controller.list(0, size)).getStatusCode());
        }
    }
}
