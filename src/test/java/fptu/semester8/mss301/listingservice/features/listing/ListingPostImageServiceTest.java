package fptu.semester8.mss301.listingservice.features.listing;

import fptu.semester8.mss301.listingservice.features.listing.application.ListingPostImageService;
import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPost;
import fptu.semester8.mss301.listingservice.features.listing.domain.ListingPostImage;
import fptu.semester8.mss301.listingservice.features.listing.mapper.ListingPostImageMapper;
import fptu.semester8.mss301.listingservice.features.listing.repository.ListingPostImageRepository;
import fptu.semester8.mss301.listingservice.features.listing.repository.ListingPostRepository;
import fptu.semester8.mss301.listingservice.features.storage.application.StorageService;
import fptu.semester8.mss301.listingservice.features.storage.domain.StorageObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingPostImageServiceTest {
    @Mock private ListingPostRepository postRepository;
    @Mock private ListingPostImageRepository imageRepository;
    @Mock private StorageService storageService;
    @Mock private ListingPostImageMapper imageMapper;

    @InjectMocks private ListingPostImageService service;

    @Test
    void uploadsImageToStorageAndReplacesCoverInsideLockedPostScope() {
        ListingPost post = new ListingPost();
        post.setId(12L);
        ListingPostImage existingCover = new ListingPostImage();
        existingCover.setId(21L);
        existingCover.setIsCover(true);
        when(postRepository.findByIdForUpdate(12L)).thenReturn(Optional.of(post));
        when(imageRepository.findByListingPost_IdAndIsCoverTrue(12L)).thenReturn(Optional.of(existingCover));
        MockMultipartFile file = file();
        when(storageService.uploadListingImage(12L, file)).thenReturn(
            new StorageObject("listings/12/a.jpg", "https://storage/a.jpg", "image/jpeg", 4));
        when(imageRepository.saveAndFlush(any(ListingPostImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.upload(12L, file, 3, true);

        InOrder order = inOrder(postRepository, storageService, imageRepository);
        order.verify(postRepository).findByIdForUpdate(12L);
        order.verify(storageService).uploadListingImage(12L, file);
        order.verify(imageRepository).saveAndFlush(existingCover);
        order.verify(imageRepository).saveAndFlush(any(ListingPostImage.class));
        assertFalse(existingCover.getIsCover());
        verify(imageMapper).toResponse(any(ListingPostImage.class));
    }

    @Test
    void settingCoverUnsetsPreviousCoverBeforeSavingSelectedImage() {
        ListingPost post = new ListingPost();
        post.setId(12L);
        ListingPostImage previous = new ListingPostImage();
        previous.setIsCover(true);
        ListingPostImage selected = new ListingPostImage();
        selected.setId(22L);
        selected.setIsCover(false);
        when(postRepository.findByIdForUpdate(12L)).thenReturn(Optional.of(post));
        when(imageRepository.findByIdAndListingPost_Id(22L, 12L)).thenReturn(Optional.of(selected));
        when(imageRepository.findByListingPost_IdAndIsCoverTrue(12L)).thenReturn(Optional.of(previous));
        when(imageRepository.saveAndFlush(any(ListingPostImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.setCover(12L, 22L);

        InOrder order = inOrder(postRepository, imageRepository);
        order.verify(postRepository).findByIdForUpdate(12L);
        order.verify(imageRepository).saveAndFlush(previous);
        order.verify(imageRepository).saveAndFlush(selected);
        assertFalse(previous.getIsCover());
        assertTrue(selected.getIsCover());
        assertEquals(22L, selected.getId());
    }

    private MockMultipartFile file() {
        return new MockMultipartFile("file", "room.jpg", "image/jpeg", new byte[] {1, 2, 3, 4});
    }
}
