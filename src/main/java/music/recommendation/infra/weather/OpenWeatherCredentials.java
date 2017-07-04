package music.recommendation.infra.weather;

import lombok.Builder;
import lombok.Getter;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Builder
public class OpenWeatherCredentials {

  @Getter
  final String apiKey;

}
