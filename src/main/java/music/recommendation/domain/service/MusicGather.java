package music.recommendation.domain.service;

import java.util.List;
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

  private static final String API_SPOTIFY = "https://api.spotify.com/v1/search";

  private String QUERY_STRING_PATTERN = "?q=genre:{genre}&type=track";

  private final RestTemplate restTemplate;

  @Autowired
  public MusicGather(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Observable<List<String>> musicsByStyle(@NonNull final String style) {
    return Observable.create(subscriber -> {
      final HttpHeaders headers = new HttpHeaders();
      LOGGER.info("Music Style Recommended is " + style);
      headers.set("Authorization", "Bearer " + "BQCJg3oshHBj5IpreRNDzQHb-GecoM1trC1vqTNo_WPrPmeL9uOqwxqwCswQ59R0cmcgJ-y-gEp6hmH7yyHKx2Z4nTEswjx4-YPOTa3J15e0F7C-eDNvMITvgnMM8lyr1RZG_GXJqvQ2L5hRBl3jbCp7VLm80g");
      final HttpEntity httpEntity = new HttpEntity(headers);
      final ResponseEntity<SpotifyResponse> response = restTemplate
          .exchange(API_SPOTIFY + QUERY_STRING_PATTERN, HttpMethod.GET, httpEntity,
              SpotifyResponse.class, style);
      subscriber.onNext(response.getBody().getTracks().getItems().stream().map(Track::getName).collect(Collectors.toList()));
      subscriber.onCompleted();
    });
  }

}
