package fptu.semester8.mss301.listingservice.features.building.api.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record BuildingPageableRequest(Integer page, Integer size) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 100;
    public Pageable toPageable() {
        int pageNumber = page == null ? DEFAULT_PAGE : page;
        int pageSize = size == null ? DEFAULT_SIZE : size;
        if (pageNumber < 0) {
            throw badRequest("page must be zero or greater");
        }
        if (pageSize <= 0) {
            throw badRequest("size must be greater than zero");
        }
        if (pageSize > MAX_SIZE) {
            throw badRequest("size must not exceed " + MAX_SIZE);
        }

        return PageRequest.of(pageNumber, pageSize);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
