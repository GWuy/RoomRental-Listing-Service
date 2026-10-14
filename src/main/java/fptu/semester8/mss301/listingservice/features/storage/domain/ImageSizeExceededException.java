package fptu.semester8.mss301.listingservice.features.storage.domain;

import org.springframework.http.HttpStatus;

public final class ImageSizeExceededException extends StorageException {
    public ImageSizeExceededException() {
        super(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds the configured maximum size");
    }
}
