package balancebite.service.util;

import balancebite.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Centralized image decision & execution helper for create/update/delete flows.
 *
 * Responsibilities:
 * - On create: upload a new file (if provided) OR accept a direct URL OR leave null.
 * - On update: upload a new file and delete the old image; switch to a new URL and delete the old image;
 *              clear the current image when both inputs are empty.
 *
 * Notes:
 * - This service delegates actual upload/delete operations to {@link CloudinaryService}.
 * - For reliability, prefer storing Cloudinary public_id and delete using that. This service currently deletes by URL.
 * - This class is stateless and thread-safe.
 */
@Service
public class ImageHandlerService {

    private static final Logger log = LoggerFactory.getLogger(ImageHandlerService.class);

    private final CloudinaryService cloudinaryService;

    public ImageHandlerService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Immutable result describing what happened during image handling.
     * - finalUrl: the resulting URL on the entity (may be null when cleared).
     * - uploadedNew: whether we uploaded a new asset.
     * - deletedOld: whether we deleted the previous asset.
     */
    public static final class ImageDecisionResult {
        private final String finalUrl;
        private final boolean uploadedNew;
        private final boolean deletedOld;

        public ImageDecisionResult(String finalUrl, boolean uploadedNew, boolean deletedOld) {
            this.finalUrl = finalUrl;
            this.uploadedNew = uploadedNew;
            this.deletedOld = deletedOld;
        }

        public String getFinalUrl()    { return finalUrl; }
        public boolean isUploadedNew() { return uploadedNew; }
        public boolean isDeletedOld()  { return deletedOld; }

        @Override
        public String toString() {
            return "ImageDecisionResult{finalUrl='%s', uploadedNew=%s, deletedOld=%s}"
                    .formatted(finalUrl, uploadedNew, deletedOld);
        }
    }

    /**
     * Main image handler with a detailed result.
     *
     * Decision order:
     * 1) If newFile present:
     *    - On update: delete currentUrl if present.
     *    - Upload file to Cloudinary and return new Cloudinary URL.
     * 2) Else if newUrl present (direct URL switch):
     *    - On update: if different from currentUrl and current exists, delete currentUrl.
     *    - Use newUrl as final.
     * 3) Else (no inputs provided):
     *    - On update: if currentUrl exists, delete it and clear (return null).
     *    - On create: keep null (no image).
     *
     * @param currentUrl existing image URL on the entity (null if none)
     * @param newFile    newly uploaded file (from client), may be null/empty
     * @param newUrl     direct image URL (from client), may be null/blank
     * @param isCreate   true when called in a create flow; false in an update flow
     * @return ImageDecisionResult with final URL and side-effect flags
     */
    public ImageDecisionResult handleImageWithResult(String currentUrl,
                                                     MultipartFile newFile,
                                                     String newUrl,
                                                     boolean isCreate) {
        boolean deletedOld = false;
        boolean uploadedNew = false;

        // 1) New file upload takes precedence.
        if (newFile != null && !newFile.isEmpty()) {
            if (!isCreate && hasText(currentUrl)) {
                safeDelete(currentUrl);
                deletedOld = true;
            }
            String uploadedUrl = cloudinaryService.uploadFile(newFile); // returns secure URL
            uploadedNew = true;
            log.info("Image uploaded; finalUrl={}", uploadedUrl);
            return new ImageDecisionResult(uploadedUrl, uploadedNew, deletedOld);
        }

        // 2) Direct URL switch (no file provided)
        if (hasText(newUrl)) {
            if (!isCreate && hasText(currentUrl) && !newUrl.equals(currentUrl)) {
                // Switching from one image to another URL -> delete the old one if it was ours
                safeDelete(currentUrl);
                deletedOld = true;
            }
            log.info("Using provided direct URL; finalUrl={}", newUrl);
            return new ImageDecisionResult(newUrl, uploadedNew, deletedOld);
        }

        // 3) No inputs provided
        if (!isCreate && hasText(currentUrl)) {
            // Update + no new inputs = clear
            safeDelete(currentUrl);
            deletedOld = true;
            log.info("Cleared image on update; finalUrl=null");
            return new ImageDecisionResult(null, uploadedNew, deletedOld);
        }

        // Create + no inputs = keep null
        log.info("No image provided; finalUrl stays as current (create flow) -> {}", currentUrl);
        return new ImageDecisionResult(currentUrl, uploadedNew, deletedOld);
    }

    /**
     * Backwards-compatible convenience wrapper.
     * Returns only the resulting URL (what you store on the entity).
     *
     * @see #handleImageWithResult(String, MultipartFile, String, boolean)
     */
    public String handleImage(String currentUrl, MultipartFile newFile, String newUrl, boolean isCreate) {
        return handleImageWithResult(currentUrl, newFile, newUrl, isCreate).getFinalUrl();
    }

    /**
     * Deletes an image by URL if present (safe to call).
     * Swallows exceptions (logs errors) so higher layers remain unaffected by Cloudinary hiccups.
     *
     * @param imageUrl URL to delete; no-op if null/blank
     */
    public void deleteImage(String imageUrl) {
        if (!hasText(imageUrl)) return;
        safeDelete(imageUrl);
    }

    // ----------------- Internal helpers -----------------

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    /**
     * Attempt to delete the asset behind the given URL.
     * This delegates to CloudinaryService (which currently parses public_id from URL).
     * Any exception is caught and logged; no exception is propagated from here.
     */
    private void safeDelete(String url) {
        try {
            cloudinaryService.deleteFileByUrl(url);
            log.info("Deleted old image: {}", url);
        } catch (Exception ex) {
            // Be defensive: we never want image deletion to break the business flow.
            log.error("Failed to delete old image {}: {}", url, ex.getMessage());
        }
    }
}
