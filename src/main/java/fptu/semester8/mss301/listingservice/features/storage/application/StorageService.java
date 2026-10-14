package fptu.semester8.mss301.listingservice.features.storage.application;

import fptu.semester8.mss301.listingservice.common.StorageValidation;
import fptu.semester8.mss301.listingservice.features.storage.application.port.out.ObjectStorage;
import fptu.semester8.mss301.listingservice.features.storage.domain.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StorageService {

    ObjectStorage objectStorage;

    ImageProperties imageProperties;

    public StorageObject uploadListingImage(
            Long listingId,
            MultipartFile file
    ) {
        validateId(listingId, "listingId");

        return uploadImage(
                "listings/" + listingId + "/",
                file
        );
    }

    private StorageObject uploadImage(
            String prefix,
            MultipartFile file
    ) {
        validateImage(file);

        String key = prefix
                + StorageKey.generate(file.getContentType());

        try {
            byte[] bytes = file.getBytes();

            byte[] header;
            try (var input = new ByteArrayInputStream(bytes)) {
                header = input.readNBytes(
                        StorageValidation.IMAGE_HEADER_LENGTH
                );
            }

            if (!StorageValidation.matchesSignature(
                    header,
                    file.getContentType()
            )) {
                throw new UnsupportedImageTypeException();
            }

            try (var uploadStream = new ByteArrayInputStream(bytes)) {
                return objectStorage.upload(
                        key,
                        uploadStream,
                        file.getContentType(),
                        bytes.length
                );
            }

        } catch (IOException ex) {
            throw new StorageUploadException();
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() <= 0) {
            throw StorageException.invalidRequest(
                    "Image must not be empty"
            );
        }

        if (file.getSize() > imageProperties.maxSize()) {
            throw new ImageSizeExceededException();
        }

        StorageValidation.validateFilename(
                file.getOriginalFilename()
        );
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw StorageException.invalidRequest(
                    "Invalid " + fieldName
            );
        }
    }

    public void deleteImage(String key) {
        StorageKey.validate(key);
        objectStorage.delete(key);
    }

    public String generateUrl(String key) {
        StorageKey.validate(key);

        if (!objectStorage.exists(key)) {
            throw new StorageObjectNotFoundException();
        }

        return objectStorage.generateUrl(key);
    }
}
