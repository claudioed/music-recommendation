package music.recommendation.domain.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;
import music.recommendation.domain.exception.SpotifyException;
import music.recommendation.domain.spotify.SpotifyResponse;
import music.recommendation.domain.spotify.Track;
import music.recommendation.infra.redis.MusicCache;
import music.recommendation.infra.spotify.SpotifyTokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Service
public class MusicGather {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicGather.class);

    private static final String API_SPOTIFY = "https://api.spotify.com/v1/search";

    private String QUERY_STRING_PATTERN = "?q=genre:{genre}&type=track";

    private final RestTemplate restTemplate;

    private final MusicCache musicCache;

    private final SpotifyTokenManager spotifyTokenManager;

    @Autowired
    public MusicGather(RestTemplate restTemplate,
                       MusicCache musicCache, SpotifyTokenManager spotifyTokenManager) {
        this.restTemplate = restTemplate;
        this.musicCache = musicCache;
        this.spotifyTokenManager = spotifyTokenManager;
    }

    @HystrixCommand(fallbackMethod = "fromCache", commandKey = "musicdata", groupKey = "music", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "100"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
    }, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "5"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")})
    public Observable<List<String>> musicsByStyle(@NonNull final String style) {
        return Observable.create(subscriber -> {
            try {
                spotifyTokenManager.token().subscribe(spotifyToken -> {
                    final HttpHeaders headers = new HttpHeaders();
                    LOGGER.info("Music Style Recommended is " + style);
                    headers.set("Authorization", "Bearer "
                            + spotifyToken.getAccessToken());
                    final HttpEntity httpEntity = new HttpEntity(headers);
                    final ResponseEntity<SpotifyResponse> response = restTemplate
                            .exchange(API_SPOTIFY + QUERY_STRING_PATTERN, HttpMethod.GET, httpEntity,
                                    SpotifyResponse.class, style);
                    final List<String> musics = response.getBody().getTracks().getItems().stream()
                            .map(Track::getName).collect(Collectors.toList());
                    this.musicCache.addMusics(style, musics);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(musics);
                        subscriber.onCompleted();
                    }
                }, Observable::error);
            } catch (Exception ex) {
                LOGGER.error("Error on query musics", ex);
                Observable.error(new SpotifyException(ex));
            }
        });
    }

    public Observable<List<String>> fromCache(@NonNull final String style) {
        LOGGER.info("RETRIEVE DATA FROM MUSIC CACHE...");
        final Optional<List<String>> musics = this.musicCache.getMusics(style);
        return Observable.just(musics.get());
    }

}
