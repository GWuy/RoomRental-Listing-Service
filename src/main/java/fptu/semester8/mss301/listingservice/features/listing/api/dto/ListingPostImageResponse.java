package fptu.semester8.mss301.listingservice.features.listing.api.dto;

public record ListingPostImageResponse(
    Long id,
    Long listingPostId,
    String objectKey,
    String imageUrl,
    Integer sortOrder,
    Boolean isCover,
    Long createdAt
) {
}
