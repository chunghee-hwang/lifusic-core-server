package dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreTempFileResponseDto {
    private String originalFileName;
    private String contentType;
    private String tempFilePath;
    private long fileSize;

    public FileDto toFileDto() {
        return FileDto.builder()
                .tempFilePath(this.tempFilePath)
                .originalFileName(this.originalFileName)
                .contentType(this.contentType)
                .size(this.fileSize)
                .build();
    }
}
