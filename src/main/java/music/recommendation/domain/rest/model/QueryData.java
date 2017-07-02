package music.recommendation.domain.rest.model;

import com.google.common.base.Strings;
import java.util.Objects;
import lombok.Builder;
import lombok.Value;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Value
@Builder
public class QueryData {

  Double lat;

  Double lon;

  String city;

  public boolean isByCoordinate() {
    return Objects.nonNull(this.lat) && Objects.nonNull(this.lon);
  }

  public boolean isValid() {
    return Objects.nonNull(this.lat) && Objects.nonNull(this.lon) || !Strings
        .isNullOrEmpty(this.city);
  }

}
