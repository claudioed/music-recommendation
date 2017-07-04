package music.recommendation.infra.spotify;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Spotify Token Service
 * Created by claudio on 04/07/17.
 */
@Service
public class SpotifyTokenManager {

    private static final String AUTH_URL = "https://accounts.spotify.com/api/token";

    private final SpotifyCredentials spotifyCredentials;

    private final RestTemplate restTemplate;

    private final Cache<String, SpotifyToken> tokenCache = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(3500, TimeUnit.SECONDS).build();

    @Autowired
    public SpotifyTokenManager(SpotifyCredentials spotifyCredentials, RestTemplate restTemplate) {
        this.spotifyCredentials = spotifyCredentials;
        this.restTemplate = restTemplate;
    }

    public Observable<SpotifyToken> token() {
        final SpotifyToken token = tokenCache.getIfPresent("token");
        if (Objects.nonNull(token)) {
            return Observable.just(token);
        }
        return Observable.create(subscriber -> {
            final SpotifyToken newToken = auth();
            tokenCache.put("token",newToken);
            if(!subscriber.isUnsubscribed()){
                subscriber.onNext(newToken);
                subscriber.onCompleted();
            }
        });
    }

    @SneakyThrows
    private SpotifyToken auth() {
        final String auth = spotifyCredentials.getClientId() + ":" + spotifyCredentials.getClientSecret();
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic "
                + Base64.getEncoder().encodeToString(auth.getBytes("utf-8")));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> data = new HttpEntity<>(params, headers);
        final ResponseEntity<SpotifyToken> tokenResponse = restTemplate.postForEntity(AUTH_URL, data, SpotifyToken.class);
        return tokenResponse.getBody();
    }

}
