package music.recommendation.domain.service;

import music.recommendation.infra.redis.MusicCache;
import music.recommendation.infra.spotify.SpotifyTokenManager;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Music Gather Test
 * Created by claudio on 04/07/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class MusicGatherTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SpotifyTokenManager spotifyTokenManager;

    @Mock
    private MusicCache musicCache;

    @InjectMocks
    private MusicGather musicGather;



}
