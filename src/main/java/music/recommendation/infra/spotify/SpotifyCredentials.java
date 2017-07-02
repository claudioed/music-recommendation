package music.recommendation.infra.spotify;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Value
@Builder
public class SpotifyCredentials {

  @NonNull
  String clientId;

  @NonNull
  String clientSecret;

}
