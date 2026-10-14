package fptu.semester8.mss301.listingservice.features.storage.domain;

import org.springframework.http.HttpStatus;

public final class StorageObjectNotFoundException extends StorageException {
    public StorageObjectNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Image not found");
    }
}
