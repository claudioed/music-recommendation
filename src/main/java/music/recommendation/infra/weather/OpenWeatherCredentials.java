package music.recommendation.infra.weather;

import lombok.Builder;
import lombok.Value;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Value
@Builder
public class OpenWeatherCredentials {

  String apiKey;

}
