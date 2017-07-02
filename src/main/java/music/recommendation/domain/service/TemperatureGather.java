package music.recommendation.domain.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.weather.CurrentWeather;
import music.recommendation.infra.weather.OpenWeatherCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

/**
 * @author claudioed on 01/07/17. Project music-recommendation
 */
@Service
public class TemperatureGather {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureGather.class);

  private final Cache<String, CurrentWeather> cache = CacheBuilder
      .newBuilder().maximumSize(100).expireAfterWrite(24L, TimeUnit.HOURS).build();

  private final RestTemplate restTemplate;

  private final OpenWeatherCredentials openWeatherCredentials;

  @Autowired
  public TemperatureGather(RestTemplate restTemplate, OpenWeatherCredentials openWeatherCredentials) {
    this.restTemplate = restTemplate;
    this.openWeatherCredentials = openWeatherCredentials;
  }

  @HystrixCommand(fallbackMethod = "fromCache")
  public Observable<CurrentWeather> weatherData(@NonNull QueryData queryData){
    if(queryData.isByCoordinate()){
      return Observable.create(subscriber -> {
        LOGGER.info("QUERY BY COORDINATE...");
        final CurrentWeather data = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={appid}", CurrentWeather.class,queryData.getLat(),queryData.getLon(),this.openWeatherCredentials.getApiKey());
        subscriber.onNext(data);
        subscriber.onCompleted();
      });
    }
    return Observable.create(subscriber -> {
      LOGGER.info("QUERY BY CITY...");
      final CurrentWeather data = restTemplate
          .getForObject("http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}",
              CurrentWeather.class, queryData.getCity(), this.openWeatherCredentials.getApiKey());
      LOGGER.info(String.format("Current temperature is %s (kelvin) ",data.getMain().getTemp()));
      subscriber.onNext(data);
      subscriber.onCompleted();
    });
  }

  public Observable<CurrentWeather> fromCache(@NonNull QueryData queryData){
    return Observable.just(this.cache.getIfPresent(queryData));
  }

}
