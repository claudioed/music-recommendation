package music.recommendation.infra.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Spotify token
 * Created by claudio on 04/07/17.
 */

@Data
public class SpotifyToken {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("expires_in")
    Integer expiresIn;

}
