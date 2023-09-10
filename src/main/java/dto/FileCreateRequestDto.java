package dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class FileCreateRequestDto {
    private Long requestUserId; // 요청한 유저 아이디
    private String musicName; // 음악 제목

    private File musicTempFile;
    private File thumbnailTempFile;

    @Builder
    @Getter
    public static class File {
        // producer 측에서 임시로 저장한 파일 경로

        private String tempFilePath;
        private String originalFileName;
        private String contentType;
        private Long size;
    }
}
