package fptu.semester8.mss301.listingservice.features.listing.application;

import fptu.semester8.mss301.listingservice.common.id.SnowflakeIdGenerator;
import fptu.semester8.mss301.listingservice.features.listing.api.dto.ListingPostImageResponse;
import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPostImage;
import fptu.semester8.mss301.listingservice.features.listing.mapper.ListingPostImageMapper;
import fptu.semester8.mss301.listingservice.features.listing.repository.ListingPostImageRepository;
import fptu.semester8.mss301.listingservice.features.listing.repository.ListingPostRepository;
import fptu.semester8.mss301.listingservice.features.storage.application.StorageService;
import fptu.semester8.mss301.listingservice.features.storage.domain.StorageObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListingPostImageService {
    private static final Logger log = LoggerFactory.getLogger(ListingPostImageService.class);

    private final ListingPostRepository listingPostRepository;
    private final ListingPostImageRepository imageRepository;
    private final StorageService storageService;
    private final ListingPostImageMapper imageMapper;

    @Transactional
    public ListingPostImageResponse upload(Long listingPostId, MultipartFile file, Integer sortOrder, Boolean isCover) {
        validateImageMetadata(sortOrder, isCover);
        var post = listingPostRepository.findByIdForUpdate(listingPostId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing post not found"));

        StorageObject stored = storageService.uploadListingImage(listingPostId, file);
        registerRollbackCleanup(stored.key());

        if (isCover) {
            unsetExistingCover(listingPostId);
        }

        ListingPostImage image = new ListingPostImage();
        image.setId(SnowflakeIdGenerator.nextId());
        image.setListingPost(post);
        image.setObjectKey(stored.key());
        image.setImageUrl(stored.url());
        image.setSortOrder(sortOrder);
        image.setIsCover(isCover);
        image.setCreatedAt(System.currentTimeMillis());
        return imageMapper.toResponse(imageRepository.saveAndFlush(image));
    }

    @Transactional
    public ListingPostImageResponse setCover(Long listingPostId, Long imageId) {
        listingPostRepository.findByIdForUpdate(listingPostId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing post not found"));
        ListingPostImage image = imageRepository.findByIdAndListingPost_Id(imageId, listingPostId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing post image not found"));

        unsetExistingCover(listingPostId);
        image.setIsCover(true);
        return imageMapper.toResponse(imageRepository.saveAndFlush(image));
    }

    private void unsetExistingCover(Long listingPostId) {
        imageRepository.findByListingPost_IdAndIsCoverTrue(listingPostId).ifPresent(existing -> {
            existing.setIsCover(false);
            imageRepository.saveAndFlush(existing);
        });
    }

    private void validateImageMetadata(Integer sortOrder, Boolean isCover) {
        if (sortOrder == null || sortOrder < 0 || isCover == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortOrder must be nonnegative and isCover is required");
        }
    }

    private void registerRollbackCleanup(String key) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        storageService.deleteImage(key);
                    } catch (RuntimeException cleanupFailure) {
                        log.warn("Failed to clean up listing image after database rollback", cleanupFailure);
                    }
                }
            }
        });
    }
}
