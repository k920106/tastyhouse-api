package com.tastyhouse.file;

import com.tastyhouse.core.entity.file.UploadedFile;
import com.tastyhouse.core.service.FileCoreService;
import com.tastyhouse.file.storage.FileStorageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileCoreService fileCoreService;
    private final FileStorageStrategy fileStorageStrategy;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public Long upload(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + "." + extension;
        String datePath = LocalDate.now().format(DATE_FORMATTER);

        String filePath = fileStorageStrategy.store(file, storedFilename, datePath);

        UploadedFile uploadedFile = UploadedFile.builder()
            .originalFilename(originalFilename)
            .storedFilename(storedFilename)
            .filePath(filePath)
            .fileSize(file.getSize())
            .contentType(file.getContentType())
            .build();

        UploadedFile saved = fileCoreService.save(uploadedFile);
        return saved.getId();
    }

    public String getFileUrl(Long fileId) {
        UploadedFile file = fileCoreService.findById(fileId);
        return fileStorageStrategy.getFileUrl(file.getFilePath());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (jpg, png, gif, webp만 가능)");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다. (jpg, png, gif, webp만 가능)");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
