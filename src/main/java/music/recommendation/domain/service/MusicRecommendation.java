package music.recommendation.domain.service;

import java.util.List;
import lombok.NonNull;
import music.recommendation.domain.music.CelsiusTemperature;
import music.recommendation.domain.rest.model.QueryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Service
public class MusicRecommendation {

  private static final Logger LOGGER = LoggerFactory.getLogger(MusicRecommendation.class);

  private final TemperatureGather temperatureGather;

  private final MusicGather musicGather;

  @Autowired
  public MusicRecommendation(TemperatureGather temperatureGather, MusicGather musicGather) {
    this.temperatureGather = temperatureGather;
    this.musicGather = musicGather;
  }

  public Observable<List<String>> musics(@NonNull QueryData queryData){
    return this.temperatureGather.weatherData(queryData).map(el -> CelsiusTemperature
        .builder().kelvinValue(el.getMain().getTemp()).build())
        .doOnNext(celsiusTemperature -> LOGGER.info(String.format("Temperature is %s (Celsius)",String.valueOf(celsiusTemperature.getValue()))))
        .doOnError(Observable::error).flatMap(data ->
        musicGather.musicsByStyle(data.recommend()));
  }

}
