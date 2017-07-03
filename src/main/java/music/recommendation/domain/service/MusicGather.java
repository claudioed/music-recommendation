package music.recommendation.domain.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.NonNull;
import music.recommendation.domain.spotify.SpotifyResponse;
import music.recommendation.domain.spotify.Track;
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

  private final Cache<String, List<String>> musicCache = CacheBuilder
      .newBuilder().maximumSize(100).expireAfterWrite(24L, TimeUnit.HOURS).build();

  private static final String API_SPOTIFY = "https://api.spotify.com/v1/search";

  private String QUERY_STRING_PATTERN = "?q=genre:{genre}&type=track";

  private final RestTemplate restTemplate;

  @Autowired
  public MusicGather(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
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
      try{
        final HttpHeaders headers = new HttpHeaders();
        LOGGER.info("Music Style Recommended is " + style);
        headers.set("Authorization", "Bearer "
            + "BQANhZ52_iwghIS4oNJO4jOBxP2B44-Ds3wcdvSlagUANrw4Ad-uYw2qf7xszRMQ10ZI6z0XHVLhIH5c7q5oNuhLUSF03t087uhlZK2EA1UZ4dRRAqN8E-u-SZw-gTwAZj-L2iPF5JQ74kDpY1_7XEer_qnUcw");
        final HttpEntity httpEntity = new HttpEntity(headers);
        final ResponseEntity<SpotifyResponse> response = restTemplate
            .exchange(API_SPOTIFY + QUERY_STRING_PATTERN, HttpMethod.GET, httpEntity,
                SpotifyResponse.class, style);
        final List<String> musics = response.getBody().getTracks().getItems().stream()
            .map(Track::getName).collect(Collectors.toList());
        this.musicCache.put(style, musics);
        if(!subscriber.isUnsubscribed()){
          subscriber.onNext(musics);
          subscriber.onCompleted();
        }
      }catch (Exception ex){
        LOGGER.error("Error on query musics",ex);
        Observable.error(ex);
      }
    });
  }

  public Observable<List<String>> fromCache(@NonNull final String style) {
    LOGGER.info("RETRIEVE DATA FROM MUSIC CACHE...");
    return Observable.just(this.musicCache.getIfPresent(style));
  }

}
