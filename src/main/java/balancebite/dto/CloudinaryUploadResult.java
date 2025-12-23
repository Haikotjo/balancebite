package balancebite.dto;

public class CloudinaryUploadResult {
    private final String imageUrl;
    private final String publicId;

    public CloudinaryUploadResult(String imageUrl, String publicId) {
        this.imageUrl = imageUrl;
        this.publicId = publicId;
    }

    public String getImageUrl() { return imageUrl; }
    public String getPublicId() { return publicId; }
}
