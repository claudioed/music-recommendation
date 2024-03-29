package music.recommendation.domain.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import rx.Observable;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Service
public class MusicGather {

  private static final Logger LOGGER = LoggerFactory.getLogger(MusicGather.class);

  private static final String API_SPOTIFY = "https://api.spotify.com/v1/search";

  private String QUERY_STRING_PATTERN = "?q=genre:{genre}&type=track";

  private final AsyncRestTemplate restTemplate;

  private final MusicCache musicCache;

  private final SpotifyTokenManager spotifyTokenManager;

  @Autowired
  public MusicGather(AsyncRestTemplate restTemplate, MusicCache musicCache,
      SpotifyTokenManager spotifyTokenManager) {
    this.restTemplate = restTemplate;
    this.musicCache = musicCache;
    this.spotifyTokenManager = spotifyTokenManager;
  }

  @HystrixCommand(fallbackMethod = "fromCache", commandKey = "weatherdata", groupKey = "weather", commandProperties = {
      @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "50"),
      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
      @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "100"),
      @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "20"),
      @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
      @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
  }, threadPoolProperties = {
      @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")})
  public Observable<List<String>> musicsByStyle(@NonNull final String style) {
    return Observable.create(subscriber -> {
      if (!subscriber.isUnsubscribed()) {
        final List<String> cachedMusics = checkCache(style);
        if (!cachedMusics.isEmpty()) {
          subscriber.onNext(cachedMusics);
          subscriber.onCompleted();
        } else {
          spotifyTokenManager.token().subscribe(spotifyToken -> Observable.from(restTemplate
              .exchange(API_SPOTIFY + QUERY_STRING_PATTERN, HttpMethod.GET,
                  buildHeaders(spotifyToken.getAccessToken()),
                  SpotifyResponse.class, style)).subscribe(
              spotifyResponseResponseEntity -> {
                final List<String> musics = spotifyResponseResponseEntity.getBody().getTracks()
                    .getItems().stream()
                    .map(Track::getName).collect(Collectors.toList());
                musicCache.addMusics(style, musics);
                subscriber.onNext(musics);
                subscriber.onCompleted();
              }, ex -> {
                LOGGER.error("Error on query musics", ex);
                Observable.error(new SpotifyException(ex));
              }),Observable::error);
        }
      }
    });
  }

  private HttpEntity buildHeaders(String token) {
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    return new HttpEntity(headers);
  }

  private List<String> checkCache(@NonNull String style) {
    final Optional<List<String>> musics = this.musicCache.getMusics(style);
    if (musics.isPresent() && !musics.get().isEmpty()) {
      LOGGER.info("[MUSIC CACHE] RETRIEVE DATA FROM MUSIC CACHE...");
      return musics.get();
    }
    return new ArrayList<>();
  }

  public Observable<List<String>> fromCache(@NonNull final String style) {
    LOGGER.info("[FALLBACK] RETRIEVE DATA FROM MUSIC CACHE...");
    final Optional<List<String>> musics = this.musicCache.getMusics(style);
    return musics.map(Observable::just).orElseGet(Observable::empty);
  }

}
