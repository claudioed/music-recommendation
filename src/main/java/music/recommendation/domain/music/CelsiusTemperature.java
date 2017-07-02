package music.recommendation.domain.music;

import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Value
public class CelsiusTemperature {

  MusicStyleShuffler shuffler = new MusicStyleShuffler();

  final static Function<Double,Double> kelvinToCelsius = (kelvin)-> kelvin - 273;

  Double value;

  public String recommend(){
    return this.shuffler.style(this.value);
  }

  @Builder
  public static CelsiusTemperature of(@NonNull Double kelvinValue){
    return new CelsiusTemperature(kelvinToCelsius.apply(kelvinValue));
  }

}
