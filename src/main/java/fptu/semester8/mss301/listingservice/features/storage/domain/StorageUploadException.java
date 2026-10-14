package fptu.semester8.mss301.listingservice.features.storage.domain;

import org.springframework.http.HttpStatus;

public final class StorageUploadException extends StorageException {
    public StorageUploadException() {
        super(HttpStatus.BAD_GATEWAY, "Image upload failed");
    }
}
