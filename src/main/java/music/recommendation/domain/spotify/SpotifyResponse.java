package music.recommendation.domain.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyResponse {

  TrackSection tracks;

  @Data
  public static class TrackSection {

    List<Track> items;

  }

}
