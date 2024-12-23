package balancebite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Value("${server.base-url:http://localhost:8080/}")
    private String baseUrl;

    /**
     * Saves the uploaded file to the specified directory.
     *
     * @param file the uploaded file.
     * @return the relative file path as a String.
     */
    public String saveFile(MultipartFile file) {
        try {
            // Generate a unique file name
            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, uniqueFileName);

            // Ensure the directory exists
            log.info("Ensuring directory exists: {}", filePath.getParent().toAbsolutePath());
            Files.createDirectories(filePath.getParent());

            // Check if the file is already written
            if (Files.exists(filePath)) {
                log.warn("File already exists: {}", filePath.toAbsolutePath());
                return uniqueFileName;
            }

            // Write the file to the specified directory
            log.info("Writing file to path: {}", filePath.toAbsolutePath());
            Files.write(filePath, file.getBytes());
            log.info("File written successfully: {}", filePath.toAbsolutePath());

            // Return the relative file name
            return uniqueFileName;
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new FileStorageException("Failed to store file: " + e.getMessage(), e);
        }
    }

    /**
     * Generates the full URL for accessing the uploaded file.
     *
     * @param fileName the relative file name.
     * @return the full URL as a String.
     */
    public String getFullUrl(String fileName) {
        String fullUrl = baseUrl + "uploads/" + fileName;
        log.info("Generated full URL: {}", fullUrl);
        return fullUrl;
    }
}

class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
