package music.recommendation.domain.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author claudioed on 01/07/17. Project music-recommendation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {

  Double temp;

  Double pressure;

  Double humidity;

  @JsonProperty("temp_min")
  Double tempMin;

  @JsonProperty("temp_max")
  Double tempMax;

}
