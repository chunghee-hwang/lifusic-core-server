package com.chung.lifusic.core.service;

import com.chung.lifusic.core.exception.FileStorageException;
import dto.StoreTempFileResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Random;

@Service
public class FileStorageService {

    @Value("${file.upload.directory}")
    private String tempUploadDirectory;

    /**
     * 파일을 temp directory에 저장
     *
     * @param file
     * @param fileName
     * @return
     */
    public StoreTempFileResponseDto storeFileToTempDir(MultipartFile file, String fileName) {
        try {
            Files.createDirectories(getTempFileDirectoryPath());
            final String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            final String fileNameWithExtension = fileName + "." + fileExtension;
            Path targetLocation = this.getTempFileDirectoryPath().resolve(fileNameWithExtension);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return StoreTempFileResponseDto
                    .builder()
                    .tempFilePath(targetLocation.toAbsolutePath().toString())
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();
        } catch (Exception exception) {
            throw new FileStorageException("Could not store file.");
        }
    }

    public StoreTempFileResponseDto storeFileToTempDirWithRandomName(MultipartFile file) {
        String randomName = getRandomFileName();
        return storeFileToTempDir(file, randomName);
    }

    private Path getTempFileDirectoryPath() {
        return Paths.get(tempUploadDirectory).toAbsolutePath().normalize();
    }

    private String getRandomFileName() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString + new Date().getTime();
    }
}
