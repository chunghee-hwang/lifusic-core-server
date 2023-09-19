package com.chung.lifusic.core.service;

import com.chung.lifusic.core.common.utils.StringUtil;
import com.chung.lifusic.core.exception.FileStorageException;
import com.chung.lifusic.core.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @Value("${file.upload.temp-directory-name}")
    private String tempDirectoryName;

    /**
     * 파일을 temp directory에 저장
     */
    public FileDto storeFileToTempDir(MultipartFile file, String fileName) {
        try {
            Files.createDirectories(getTempDirectoryPath());
            final String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            final String fileNameWithExtension = fileName + "." + fileExtension;
            Path targetLocation = this.getTempDirectoryPath().resolve(fileNameWithExtension);
            final String tempFilePath = this.tempDirectoryName + "/" + fileNameWithExtension;
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return FileDto
                    .builder()
                    .tempFilePath(tempFilePath)
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
        } catch (Exception exception) {
            log.error("Fail to save file to temp directory: {}", exception.getMessage());
            throw new FileStorageException("Could not store file.");
        }
    }

    public FileDto storeFileToTempDirWithRandomName(MultipartFile file) {
        String randomName = StringUtil.getUniqueString(5);
        return storeFileToTempDir(file, randomName);
    }

    private Path getUploadDirectoryPath() {
        return Paths.get(uploadDirectory).toAbsolutePath();
    }

    private Path getTempDirectoryPath() {
        return getUploadDirectoryPath().resolve(tempDirectoryName);
    }
}
