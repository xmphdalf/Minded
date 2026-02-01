package upload;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Secure file upload utility for handling image uploads.
 * Provides validation, sanitization, and safe file storage.
 *
 * @author Minded Team
 * @version 2.0
 */
public class fileupload {

    // Configurable upload directory (use environment variable or default)
    private static final String UPLOAD_BASE_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    // Allowed file extensions
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    // Allowed MIME types
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    // File size limits (in bytes)
    private static final long MAX_FILE_SIZE_PROFILE = 5 * 1024 * 1024;  // 5MB
    private static final long MAX_FILE_SIZE_COVER = 10 * 1024 * 1024;   // 10MB
    private static final long MAX_FILE_SIZE_DEFAULT = 5 * 1024 * 1024;  // 5MB

    // Magic numbers for file type validation
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC = {0x47, 0x49, 0x46};

    /**
     * Upload file with default size limit.
     *
     * @param part File part from multipart request
     * @return Filename of uploaded file, or null if upload fails
     */
    public String uploadme(Part part) {
        return uploadFile(part, "default", MAX_FILE_SIZE_DEFAULT);
    }

    /**
     * Upload file with specified type and size limit.
     *
     * @param part File part from multipart request
     * @param uploadType Type of upload (profile, cover, default)
     * @param maxSize Maximum file size in bytes
     * @return Filename of uploaded file, or null if upload fails
     */
    public String uploadFile(Part part, String uploadType, long maxSize) {
        if (part == null) {
            System.err.println("Upload failed: Part is null");
            return null;
        }

        try {
            // Extract and sanitize filename
            String originalFilename = extractFilename(part);
            if (originalFilename == null || originalFilename.isEmpty()) {
                System.err.println("Upload failed: No filename provided");
                return null;
            }

            // Validate file size
            long fileSize = part.getSize();
            if (fileSize > maxSize) {
                System.err.println("Upload failed: File size " + fileSize + " exceeds limit " + maxSize);
                return null;
            }

            if (fileSize == 0) {
                System.err.println("Upload failed: File is empty");
                return null;
            }

            // Validate file extension
            String extension = getFileExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                System.err.println("Upload failed: Invalid file extension: " + extension);
                return null;
            }

            // Validate MIME type
            String contentType = part.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
                System.err.println("Upload failed: Invalid MIME type: " + contentType);
                return null;
            }

            // Validate file content (magic numbers)
            if (!isValidImageFile(part)) {
                System.err.println("Upload failed: File content does not match image format");
                return null;
            }

            // Generate unique filename to prevent collisions
            String uniqueFilename = generateUniqueFilename(extension);

            // Ensure upload directory exists
            File uploadDir = new File(UPLOAD_BASE_DIR);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    System.err.println("Upload failed: Could not create upload directory");
                    return null;
                }
            }

            // Save file securely
            String filePath = UPLOAD_BASE_DIR + File.separator + uniqueFilename;
            try (InputStream input = part.getInputStream()) {
                Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("✓ File uploaded successfully: " + uniqueFilename);
            return uniqueFilename;

        } catch (IOException e) {
            System.err.println("Upload failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extract filename from Part header.
     */
    private String extractFilename(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                String filename = item.substring(item.indexOf("=") + 1).trim();
                // Remove quotes
                filename = filename.replace("\"", "");
                // Sanitize filename
                return sanitizeFilename(filename);
            }
        }
        return null;
    }

    /**
     * Sanitize filename to prevent directory traversal and other attacks.
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return null;
        }

        // Remove path separators and dangerous characters
        filename = filename.replaceAll("[/\\\\]", "");
        filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Remove leading dots (hidden files)
        filename = filename.replaceAll("^\\.+", "");

        // Limit length
        if (filename.length() > 255) {
            filename = filename.substring(0, 255);
        }

        return filename;
    }

    /**
     * Get file extension from filename.
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * Generate unique filename using UUID.
     */
    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + "." + extension.toLowerCase();
    }

    /**
     * Validate file content by checking magic numbers.
     */
    private boolean isValidImageFile(Part part) {
        try (InputStream input = part.getInputStream()) {
            byte[] header = new byte[8];
            int bytesRead = input.read(header);

            if (bytesRead < 3) {
                return false;
            }

            // Check for JPEG magic number
            if (header[0] == JPEG_MAGIC[0] && header[1] == JPEG_MAGIC[1] && header[2] == JPEG_MAGIC[2]) {
                return true;
            }

            // Check for PNG magic number
            if (bytesRead >= 4 && header[0] == PNG_MAGIC[0] && header[1] == PNG_MAGIC[1] &&
                header[2] == PNG_MAGIC[2] && header[3] == PNG_MAGIC[3]) {
                return true;
            }

            // Check for GIF magic number
            if (bytesRead >= 3 && header[0] == GIF_MAGIC[0] && header[1] == GIF_MAGIC[1] && header[2] == GIF_MAGIC[2]) {
                return true;
            }

            return false;
        } catch (IOException e) {
            System.err.println("Error reading file content: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get upload directory path.
     */
    public static String getUploadDirectory() {
        return UPLOAD_BASE_DIR;
    }

    /**
     * Legacy method for backward compatibility.
     *
     * @deprecated Use uploadFile() instead
     */
    @Deprecated
    public String extractfilename(Part part) {
        return extractFilename(part);
    }
}
