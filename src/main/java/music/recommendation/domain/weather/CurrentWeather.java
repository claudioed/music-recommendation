package music.recommendation.domain.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author claudioed on 01/07/17. Project music-recommendation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {

  Coordinate coord;

  Main main;

}
