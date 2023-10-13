package service;

import com.chung.lifusic.core.common.enums.Role;
import com.chung.lifusic.core.dto.FileCreateResponseDto;
import com.chung.lifusic.core.dto.GetArtistMusicsResponseDto;
import com.chung.lifusic.core.dto.SearchRequestDto;
import com.chung.lifusic.core.entity.File;
import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.entity.User;
import com.chung.lifusic.core.repository.FileRepository;
import com.chung.lifusic.core.repository.MusicInPlaylistRepository;
import com.chung.lifusic.core.repository.MusicRepository;
import com.chung.lifusic.core.repository.UserRepository;
import com.chung.lifusic.core.service.AdminMusicService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminMusicServiceTest {
    @InjectMocks
    private AdminMusicService service;

    @Mock
    private MusicRepository musicRepository;
    @Mock
    private MusicInPlaylistRepository musicInPlaylistRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileRepository fileRepository;
    @Mock
    private RestTemplate restTemplate;

    @DisplayName("썸네일 파일이 없으면 없는 상태로 저장")
    @Test
    public void createMusicWithNoThumbnail() {
        // given
        FileCreateResponseDto fileCreateResponseDto = getFileCreateResponse(null);
        User user = getUser();
        File musicFile = getMusicFile();
        Music music = getMusic(user, 1L, musicFile, null);

        // mocking
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(fileRepository.findById(any())).willReturn(Optional.ofNullable(musicFile));
        given(musicRepository.save(any())).willReturn(music);

        // when
        service.createMusic(fileCreateResponseDto);

        // then
        verify(musicRepository).save(argThat(argument ->
                argument.getThumbnailImageFile() == null
        ));
    }

    @DisplayName("썸네일 파일과 함께 저장")
    @Test
    public void createMusicWithThumbnail() {
        // given
        FileCreateResponseDto fileCreateResponseDto = getFileCreateResponse(2L);
        User user = getUser();
        File musicFile = getMusicFile();
        File thumbnailFile = getThumbnailImageFile();
        Music music = getMusic(user, 1L, musicFile, thumbnailFile);

        // mocking
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(fileRepository.findById(any())).willReturn(Optional.ofNullable(musicFile));
        given(musicRepository.save(any())).willReturn(music);

        // when
        service.createMusic(fileCreateResponseDto);

        // then
        verify(musicRepository).save(argThat(argument ->
                argument.getThumbnailImageFile() != null
        ));
    }

    @DisplayName("음악에 있는 음악 파일과 이미지 파일을 모두 가져오기")
    @Test
    public void getAllFileIdsInMusics() {
        // given
        User user = getUser();
        List<Long> musicIds = Arrays.asList(1L, 2L, 3L);
        List<Music> musics = getMusicsByIds(user, musicIds);

        // mocking
        given(musicRepository.findMusicsByIds(musicIds)).willReturn(musics);

        // when
        List<Long> fileIds = service.getAllFileIdsInMusics(musicIds);

        // then
        Assertions.assertEquals(musicIds.size() * 2, fileIds.size());
    }

    /* downloadMusicFile 는 파일 작업이므로 테스트 건너띔 */

    @DisplayName("아티스트 아이디로 음악 가져오기 - 키워드가 없으면 전체 검색")
    @Test
    public void searchAllWhenKeywordEmpty() {
        // given
        List<Long> musicIds = Arrays.asList(1L, 2L, 3L);
        User user = getUser();
        SearchRequestDto request = new SearchRequestDto();
        request.setKeyword(null);
        request.setLimit(5);
        request.setPage(1);
        request.setOrderBy("name");
        request.setOrderDirection("asc");
        Page<Music> musics = getMusicPage(user, musicIds);

        // mocked
        given(musicRepository.findMusics(anyLong(), any(Pageable.class))).willReturn(musics);

        // when
        GetArtistMusicsResponseDto response = service.getMusicsByArtistId(user.getId(), request);

        // then
        Assertions.assertEquals(musicIds.size(), response.getMusics().size());
        verify(musicRepository, times(1)).findMusics(anyLong(), any(Pageable.class));
        verify(musicRepository, times(0)).findMusics(anyLong(), anyString(), any(Pageable.class));
    }

    @DisplayName("아티스트 아이디로 음악 가져오기 - 키워드로 검색")
    @Test
    public void searchWithKeyword() {
        // given
        List<Long> musicIds = Arrays.asList(1L, 2L, 3L);
        User user = getUser();
        SearchRequestDto request = new SearchRequestDto();
        request.setKeyword("music title");
        request.setLimit(5);
        request.setPage(1);
        request.setOrderBy("name");
        request.setOrderDirection("asc");
        Page<Music> musics = getMusicPage(user, musicIds);

        // mocked
        given(musicRepository.findMusics(anyLong(), anyString(), any(Pageable.class))).willReturn(musics);

        // when
        GetArtistMusicsResponseDto response = service.getMusicsByArtistId(user.getId(), request);

        // then
        Assertions.assertEquals(musicIds.size(), response.getMusics().size());
        verify(musicRepository, times(0)).findMusics(anyLong(), any(Pageable.class));
        verify(musicRepository, times(1)).findMusics(anyLong(), anyString(), any(Pageable.class));
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .email("test@email.com")
                .password("1234")
                .role(Role.ADMIN)
                .build();
    }

    private File getMusicFile() {
        return File.builder()
                .id(1L)
                .contentType("audio/mpeg")
                .originalFileName("music.mp3")
                .path("path/to/music.mp3")
                .size(15000L)
                .build();
    }

    private File getThumbnailImageFile() {
        return File.builder()
                .id(1L)
                .contentType("image/png")
                .originalFileName("thumbnail.png")
                .path("path/to/thumbnail.png")
                .size(1500L)
                .build();
    }

    private Music getMusic(User artist, Long id, File musicFile, File thumbnailFile) {
        return Music.builder()
                .musicFile(musicFile)
                .thumbnailImageFile(thumbnailFile)
                .name("music")
                .artist(artist)
                .artistName(artist.getName())
                .id(id)
                .build();
    }

    private List<Music> getMusicsByIds(User artist, List<Long> musicIds) {
        return musicIds.stream().map(
                        musicId -> getMusic(
                                artist,
                                musicId,
                                getMusicFile(),
                                getThumbnailImageFile()
                        ))
                .toList();
    }

    private Page<Music> getMusicPage(User artist, List<Long> musicIds) {
        List<Music> musics = getMusicsByIds(artist, musicIds);
        Pageable pageable = PageRequest.of(1, 5, Sort.by("name").ascending());
        return new PageImpl<>(musics, pageable, 100);
    }

    private FileCreateResponseDto getFileCreateResponse(Long thumbnailFileId) {
        FileCreateResponseDto fileCreateResponseDto = new FileCreateResponseDto();
        FileCreateResponseDto.Content content = new FileCreateResponseDto.Content();
        content.setMusicFileId(1L);
        content.setMusicName("music1");
        content.setThumbnailFileId(thumbnailFileId);
        fileCreateResponseDto.setContent(content);
        fileCreateResponseDto.setSuccess(true);
        fileCreateResponseDto.setRequestUserId(1L);
        return fileCreateResponseDto;
    }
}
