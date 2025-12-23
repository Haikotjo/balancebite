package balancebite.service;

import balancebite.dto.CloudinaryUploadResult;
import balancebite.repository.MealImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CloudinaryService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;
    private final MealImageRepository mealImageRepository;


    public CloudinaryService(
            @Value("${CLOUDINARY_CLOUD_NAME}") String cloudName,
            @Value("${CLOUDINARY_API_KEY}") String apiKey,
            @Value("${CLOUDINARY_API_SECRET}") String apiSecret,
            MealImageRepository mealImageRepository) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
        this.mealImageRepository = mealImageRepository;
    }


    public CloudinaryUploadResult uploadFile(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            log.info("⏫ Uploaded to Cloudinary: {} (publicId={})", url, publicId);
            return new CloudinaryUploadResult(url, publicId);
        } catch (IOException e) {
            log.error("❌ Failed to upload to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

    public void deleteFileByPublicId(String publicId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result =
                    (Map<String, Object>) cloudinary.uploader()
                            .destroy(publicId, ObjectUtils.emptyMap());

            log.info("Deleted image from Cloudinary (publicId={}): {}", publicId, result);
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary (publicId={}): {}", publicId, e.getMessage());
        }
    }

    public void deleteFileIfUnused(String publicId) {
        long usageCount = mealImageRepository.countByPublicId(publicId);
        if (usageCount <= 1) {
            deleteFileByPublicId(publicId);
        } else {
            log.info("Skip delete Cloudinary file {}, still used {} times", publicId, usageCount);
        }
    }

}
