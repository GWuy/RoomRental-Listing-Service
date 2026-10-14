package fptu.semester8.mss301.listingservice.features.storage.domain;

import org.springframework.http.HttpStatus;

public final class StorageDeleteException extends StorageException {
    public StorageDeleteException() {
        super(HttpStatus.BAD_GATEWAY, "Image deletion failed");
    }
}
