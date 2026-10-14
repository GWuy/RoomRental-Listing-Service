package fptu.semester8.mss301.listingservice.features.storage.mapper;

import fptu.semester8.mss301.listingservice.features.storage.api.dto.PresignedUrlResponse;
import fptu.semester8.mss301.listingservice.features.storage.api.dto.UploadImageResponse;
import fptu.semester8.mss301.listingservice.features.storage.domain.StorageObject;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StorageMapper {
    UploadImageResponse toUploadImageResponse(StorageObject object);

    PresignedUrlResponse toPresignedUrlResponse(String url);
}
