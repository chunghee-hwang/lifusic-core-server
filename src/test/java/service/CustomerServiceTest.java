package service;

import com.chung.lifusic.musicservice.common.enums.Role;
import com.chung.lifusic.musicservice.dto.*;
import com.chung.lifusic.musicservice.entity.*;
import com.chung.lifusic.musicservice.exception.ForbiddenException;
import com.chung.lifusic.musicservice.exception.NotFoundException;
import com.chung.lifusic.musicservice.repository.MusicInPlaylistRepository;
import com.chung.lifusic.musicservice.repository.MusicRepository;
import com.chung.lifusic.musicservice.repository.PlaylistRepository;
import com.chung.lifusic.musicservice.repository.UserRepository;
import com.chung.lifusic.musicservice.service.CustomerMusicService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @InjectMocks
    private CustomerMusicService service;

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MusicInPlaylistRepository musicInPlaylistRepository;

    @DisplayName("음악 가져오기 - 없는 음악")
    @Test
    public void getMusicNotExist() {
        // given
        final Long musicId = 1L;

        // mocking
        given(musicRepository.findMusic(anyLong())).willThrow(NoSuchElementException.class);

        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            // when
            service.getMusic(musicId);
        });
    }

    @DisplayName("음악 가져오기 - 존재하는 음악")
    @Test
    public void getMusicThatExist() {
        // given
        final Long musicId = 1L;
        final Music music = getMusic(musicId);

        // mocking
        given(musicRepository.findMusic(anyLong())).willReturn(Optional.ofNullable(music));

        // when
        GetMusicResponseDto response = service.getMusic(musicId);

        // then
        Assertions.assertNotNull(music);
        Assertions.assertEquals(music.getId(), response.getMusicId());
    }

    @DisplayName("음악 검색 - 키워드로 검색")
    @Test
    public void searchMusicsWithKeyword() {
        // given
        final List<Long> musicIds = Arrays.asList(1L, 2L);
        final String keyword = "music name";
        SearchRequestDto request = getSearchRequest(keyword);
        Page<Music> musics = getMusicPage(musicIds);

        // mocking
        given(musicRepository.searchMusics(anyString(), any(Pageable.class))).willReturn(musics);

        // when
        SearchMusicResponseDto response = service.searchMusics(request);

        // then
        Assertions.assertEquals(response.getMusics().size(), musics.getContent().size());
        verify(musicRepository, times(1)).searchMusics(anyString(), any(Pageable.class));
        verify(musicRepository, times(0)).searchMusics(any(Pageable.class));
    }

    @DisplayName("음악 검색 - 키워드가 없으면 전체 검색")
    @Test
    public void searchMusicsWithEmptyKeyword() {
        // given
        final List<Long> musicIds = Arrays.asList(1L, 2L);
        final String keyword = "";
        SearchRequestDto request = getSearchRequest(keyword);
        Page<Music> musics = getMusicPage(musicIds);

        // mocking
        given(musicRepository.searchMusics(any(Pageable.class))).willReturn(musics);

        // when
        SearchMusicResponseDto response = service.searchMusics(request);

        // then
        Assertions.assertEquals(response.getMusics().size(), musics.getContent().size());
        verify(musicRepository, times(0)).searchMusics(anyString(), any(Pageable.class));
        verify(musicRepository, times(1)).searchMusics(any(Pageable.class));
    }

    @DisplayName("플레이리스트 생성 - 유저가 없다면 Forbidden 에러 던짐")
    @Test
    public void createPlaylistFailed() {
        // given
        User user = getUser();
        CreatePlaylistRequestDto request = getCreatePlaylistRequest();

        // mocking
        given(userRepository.findById(anyLong())).willThrow(NoSuchElementException.class);

        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            // when
            service.createPlaylist(user.getId(), request);
        });
    }

    @DisplayName("플레이리스트 생성 - 유저가 있을 경우 정상 생성")
    @Test
    public void createPlaylistSuccess() {
        // given
        User user = getUser();
        CreatePlaylistRequestDto request = getCreatePlaylistRequest();
        Playlist playlist = getPlaylist(1L);

        // mocking
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(playlistRepository.save(any())).willReturn(playlist);

        // when
        CreatePlaylistResponseDto response = service.createPlaylist(user.getId(), request);

        // then
        Assertions.assertEquals(playlist.getId(), response.getPlaylistId());
    }

    @DisplayName("고객의 모든 플레이리스트 가져오기")
    @Test
    public void getAllPlaylist() {
        // given
        User user = getUser();
        List<Long> playlistIds = Arrays.asList(1L, 2L);
        List<Playlist> playlists = playlistIds.stream().map(this::getPlaylist).toList();

        // mocking
        given(playlistRepository.getPlaylistsByOwnerId(user.getId())).willReturn(playlists);

        // when
        List<PlaylistDto> response = service.getAllPlaylist(user.getId());

        // then
        Assertions.assertTrue(
                playlistIds.containsAll(response.stream().map(PlaylistDto::getId).toList())
        );
    }

    @DisplayName("음악을 플레이리스트에 추가 - 추가하려는 음악이 없으면 에러 던지기")
    @Test
    public void addMusicToPlaylistFailed() {
        // given
        AddMusicToPlaylistRequestDto request = getAddMusicToPlaylistRequest();

        // mocking
        given(musicRepository.findById(anyLong())).willThrow(NoSuchElementException.class);

        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            // when
            service.addMusicToPlaylist(request);
        });
    }

    @DisplayName("음악을 플레이리스트에 추가 - 추가하려는 플레이리스트가 없으면 에러 던지기")
    @Test
    public void addMusicToPlaylistFailed2() {
        // given
        AddMusicToPlaylistRequestDto request = getAddMusicToPlaylistRequest();
        Music music = getMusic(1L);
        // mocking
        given(musicRepository.findById(anyLong())).willReturn(Optional.of(music));
        given(playlistRepository.findById(anyLong())).willThrow(NoSuchElementException.class);

        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            // when
            service.addMusicToPlaylist(request);
        });
    }

    @DisplayName("음악을 플레이리스트에 추가 - 플레이리스트에 중복된 음악이 없으면 추가")
    @Test
    public void addMusicToPlaylistWithNewMusic() {
        // given
        AddMusicToPlaylistRequestDto request = getAddMusicToPlaylistRequest();
        Music music = getMusic(1L);
        Playlist playlist = getPlaylist(1L);

        // mocking
        given(musicRepository.findById(anyLong())).willReturn(Optional.of(music));
        given(playlistRepository.findById(anyLong())).willReturn(Optional.of(playlist));
        given(musicInPlaylistRepository.existsByPlaylistIdAndMusicId(anyLong(), anyLong())).willReturn(false);

        // when
        CommonResponseDto response = service.addMusicToPlaylist(request);

        // then
        verify(musicInPlaylistRepository, times(1)).save(any());
        Assertions.assertTrue(response.isSuccess());
    }

    @DisplayName("음악을 플레이리스트에 추가 - 플레이리스트에 중복된 음악이 있으면 추가하지 않음")
    @Test
    public void addMusicToPlaylistWithExistMusic() {
        // given
        AddMusicToPlaylistRequestDto request = getAddMusicToPlaylistRequest();
        Music music = getMusic(1L);
        Playlist playlist = getPlaylist(1L);

        // mocking
        given(musicRepository.findById(anyLong())).willReturn(Optional.of(music));
        given(playlistRepository.findById(anyLong())).willReturn(Optional.of(playlist));
        given(musicInPlaylistRepository.existsByPlaylistIdAndMusicId(anyLong(), anyLong())).willReturn(true);

        // when
        CommonResponseDto response = service.addMusicToPlaylist(request);

        // then
        verify(musicInPlaylistRepository, times(0)).save(any());
        Assertions.assertTrue(response.isSuccess());
    }

    @DisplayName("플레이리스트에 있는 음악 모두 가져오기")
    @Test
    public void getAllMusicInPlaylistAscending() {
        // given
        Playlist playlist = getPlaylist(1L);
        SortRequestDto request = getSortRequest("name", "asc");
        List<Long> musicIds = Arrays.asList(1L, 2L, 3L);
        List<MusicInPlaylist> musicInPlaylists = getMusicsInPlaylist(musicIds, playlist.getId());

        // mocking
        given(musicInPlaylistRepository.findMusicsInPlaylist(anyLong(), any(Sort.class))).willReturn(musicInPlaylists);

        // when
        List<MusicInPlaylistDto> response = service.getAllMusicInPlaylist(playlist.getId(), request);

        // then
        boolean requiredValueNotNull = response.stream().allMatch(musicInPlaylist -> musicInPlaylist.getMusicInPlaylistId() != null &&
                musicInPlaylist.getMusicId() != null &&
                musicInPlaylist.getMusicName() != null &&
                musicInPlaylist.getArtistName() != null &&
                musicInPlaylist.getFileId() != null);
        Assertions.assertTrue(requiredValueNotNull);
    }

    @DisplayName("플레이리스트에 있는 음악 삭제")
    @Test
    public void deleteMusicsInPlaylist() {
        // given
        DeleteMusicsInPlaylistRequestDto request = getDeleteMusicsInPlaylistRequest();

        // when
        CommonResponseDto response = service.deleteMusicsInPlaylist(request);

        // then
        Assertions.assertTrue(response.isSuccess());
    }

    private SearchRequestDto getSearchRequest(String keyword) {
        SearchRequestDto request = new SearchRequestDto();
        request.setKeyword(keyword);
        request.setLimit(5);
        request.setPage(1);
        request.setOrderBy("name");
        request.setOrderDirection("asc");
        return request;
    }

    private User getUser() {
        return User.builder()
                .id(1L)
                .name("test")
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

    private Music getMusic(Long id) {
        User user = getUser();
        return Music.builder()
                .musicFile(getMusicFile())
                .thumbnailImageFile(getThumbnailImageFile())
                .name("music")
                .artist(getUser())
                .artistName(user.getName())
                .id(id)
                .build();
    }

    private List<Music> getMusicsByIds(List<Long> musicIds) {
        return musicIds.stream().map(
                        this::getMusic)
                .toList();
    }

    private Page<Music> getMusicPage(List<Long> musicIds) {
        List<Music> musics = getMusicsByIds(musicIds);
        Pageable pageable = PageRequest.of(1, 5, Sort.by("name").ascending());
        return new PageImpl<>(musics, pageable, 100);
    }

    private CreatePlaylistRequestDto getCreatePlaylistRequest() {
        return CreatePlaylistRequestDto.builder()
                .name("Playlist 1")
                .build();
    }

    private Playlist getPlaylist(Long id) {
        Playlist playlist = Playlist.builder()
                .owner(getUser())
                .id(id)
                .name("Playlist")
                .build();
        LocalDateTime now = LocalDateTime.now();
        playlist.setCreatedDate(now);
        playlist.setUpdatedDate(now);
        return playlist;
    }

    private AddMusicToPlaylistRequestDto getAddMusicToPlaylistRequest() {
        AddMusicToPlaylistRequestDto request = new AddMusicToPlaylistRequestDto();
        request.setPlaylistId(1L);
        request.setMusicId(1L);
        return request;
    }

    private SortRequestDto getSortRequest(String orderBy, String orderDirection) {
        SortRequestDto request = new SearchRequestDto();
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        return request;
    }

    private List<MusicInPlaylist> getMusicsInPlaylist(List<Long> musicsIds, Long playlistId) {
        final Playlist playlist = getPlaylist(playlistId);
        return musicsIds.stream().map(musicId -> MusicInPlaylist.builder()
                .music(getMusic(musicId))
                .playlist(playlist)
                .id(playlistId + musicId)
                .build()).toList();
    }

    private DeleteMusicsInPlaylistRequestDto getDeleteMusicsInPlaylistRequest() {
        DeleteMusicsInPlaylistRequestDto request = new DeleteMusicsInPlaylistRequestDto();
        request.setMusicInPlaylistIds(Arrays.asList(1L, 2L, 3L, 5L));
        return request;
    }
}
