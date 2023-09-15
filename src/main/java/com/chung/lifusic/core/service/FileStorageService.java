package com.chung.lifusic.core.service;

import com.chung.lifusic.core.common.utils.StringUtil;
import com.chung.lifusic.core.exception.FileStorageException;
import com.chung.lifusic.core.dto.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${file.upload.directory}")
    private String tempUploadDirectory;

    /**
     * 파일을 temp directory에 저장
     */
    public FileDto storeFileToTempDir(MultipartFile file, String fileName) {
        try {
            Files.createDirectories(getTempFileDirectoryPath());
            final String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            final String fileNameWithExtension = fileName + "." + fileExtension;
            Path targetLocation = this.getTempFileDirectoryPath().resolve(fileNameWithExtension);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return FileDto
                    .builder()
                    .tempFilePath(targetLocation.toAbsolutePath().toString())
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();
        } catch (Exception exception) {
            throw new FileStorageException("Could not store file.");
        }
    }

    public FileDto storeFileToTempDirWithRandomName(MultipartFile file) {
        String randomName = StringUtil.getRandomString(5);
        return storeFileToTempDir(file, randomName);
    }

    private Path getTempFileDirectoryPath() {
        return Paths.get(tempUploadDirectory).toAbsolutePath().normalize();
    }
}
