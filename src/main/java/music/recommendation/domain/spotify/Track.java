package music.recommendation.domain.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

  String id;

  String name;

}
