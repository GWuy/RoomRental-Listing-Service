package fptu.semester8.mss301.listingservice.features.listing.mapper;

import fptu.semester8.mss301.listingservice.features.listing.api.dto.ListingPostImageResponse;
import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ListingPostImageMapper {
    @Mapping(target = "listingPostId", source = "listingPost.id")
    ListingPostImageResponse toResponse(ListingPostImage image);
}
