package music.recommendation.infra.spotify;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Builder
public class SpotifyCredentials {

  @NonNull
  @Getter
  String clientId;

  @NonNull
  @Getter
  String clientSecret;

}
