package dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FileCreateResponseDto {
    private boolean isSuccess;
    private Content content;

    @Getter
    @Setter
    public static class Content {
        private Long requestUserId; // 요청한 사람 아이디
        private String musicName; // 음악 이름
        private Long musicFileId; // 음악 파일 아이디
        private Long thumbnailFileId; // 썸네일 파일 아이디
    }
}
