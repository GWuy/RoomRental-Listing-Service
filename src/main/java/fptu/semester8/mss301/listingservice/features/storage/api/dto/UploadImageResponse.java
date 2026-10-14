package fptu.semester8.mss301.listingservice.features.storage.api.dto;

public record UploadImageResponse(String key, String url, String contentType, long size) {
}
