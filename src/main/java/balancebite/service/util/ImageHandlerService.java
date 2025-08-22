package balancebite.service.util;

import balancebite.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageHandlerService {

    private final CloudinaryService cloudinaryService;

    public ImageHandlerService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Handles image logic for create and update flows.
     * - On update: deletes old image if a new file is uploaded.
     * - On clear: deletes current image if both new file and URL are empty.
     * - On fallback: uses direct image URL.
     *
     * @param currentUrl existing image URL on entity
     * @param newFile    uploaded MultipartFile
     * @param newUrl     optional direct image URL
     * @param isCreate   true if used in a create flow (no delete)
     * @return the new or existing image URL, or null if cleared
     */
    public String handleImage(String currentUrl, MultipartFile newFile, String newUrl, boolean isCreate) {
        if (newFile != null && !newFile.isEmpty()) {
            if (!isCreate && currentUrl != null) {
                cloudinaryService.deleteFileByUrl(currentUrl);
            }
            return cloudinaryService.uploadFile(newFile);
        }

        if (!isCreate
                && (newFile == null || newFile.isEmpty())
                && (newUrl == null || newUrl.isBlank())
                && currentUrl != null) {
            cloudinaryService.deleteFileByUrl(currentUrl);
            return null;
        }

        if ((newFile == null || newFile.isEmpty())
                && newUrl != null && !newUrl.isBlank()) {
            return newUrl;
        }

        return currentUrl;
    }

    /**
     * Deletes an image by URL if present.
     * Safe to call; ignores null/blank.
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        cloudinaryService.deleteFileByUrl(imageUrl);
    }
}
