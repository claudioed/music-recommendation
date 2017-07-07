package music.recommendation.domain.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import java.util.Optional;
import lombok.NonNull;
import music.recommendation.domain.exception.OpenWeatherException;
import music.recommendation.domain.exception.PlaceNotFoundException;
import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.weather.CurrentWeather;
import music.recommendation.infra.redis.WeatherCache;
import music.recommendation.infra.redis.WeatherCache.Strategy;
import music.recommendation.infra.weather.OpenWeatherCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author claudioed on 01/07/17. Project music-recommendation
 */
@Service
public class TemperatureGather {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureGather.class);

  private final AsyncRestTemplate restTemplate;

  private final OpenWeatherCredentials openWeatherCredentials;

  private final WeatherCache weatherCache;

  @Autowired
  public TemperatureGather(AsyncRestTemplate restTemplate,
      OpenWeatherCredentials openWeatherCredentials,
      WeatherCache weatherCache) {
    this.restTemplate = restTemplate;
    this.openWeatherCredentials = openWeatherCredentials;
    this.weatherCache = weatherCache;
  }

  @HystrixCommand(fallbackMethod = "fromCache", commandKey = "weatherdata", groupKey = "weather", commandProperties = {
      @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "50"),
      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
      @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "100"),
      @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "20"),
      @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
      @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
  }, threadPoolProperties = {
      @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")})
  public Observable<CurrentWeather> weatherData(@NonNull QueryData queryData) {
    if (queryData.isByCoordinate()) {
      return Observable.create(subscriber -> {
        if (!subscriber.isUnsubscribed()) {
          final Optional<CurrentWeather> cachedWeather = checkCacheByCoord(queryData.getLat(),
              queryData.getLon());
          if (cachedWeather.isPresent()) {
            LOGGER.info("[CITY CACHE BY COORD] RETRIEVE TEMPERATURE FROM CACHE...");
            subscriber.onNext(cachedWeather.get());
            subscriber.onCompleted();
          } else {
            Observable.from(restTemplate
                .getForEntity(
                    "http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={appid}",
                    CurrentWeather.class, queryData.getLat(), queryData.getLon(),
                    this.openWeatherCredentials.getApiKey())).subscribe(
                currentWeatherResponseEntity -> {
                  weatherCache.addBy(Strategy.COORDINATE, currentWeatherResponseEntity.getBody());
                  subscriber.onNext(currentWeatherResponseEntity.getBody());
                  subscriber.onCompleted();
                }, new Action1<Throwable>() {
                  @Override
                  public void call(Throwable e) {
                    LOGGER.error("Error on query city by coordinate", e);
                    if (e instanceof HttpClientErrorException) {
                      subscriber.onError(new PlaceNotFoundException());
                    } else {
                      subscriber.onError(new OpenWeatherException());
                    }
                  }
                });
          }
        }
      });
    }
    return Observable.create(subscriber -> {
      if (!subscriber.isUnsubscribed()) {
        final Optional<CurrentWeather> cachedWeather = checkCacheByCityName(queryData.getCity());
        if (cachedWeather.isPresent()) {
          LOGGER.info("[CITY CACHE BY NAME] RETRIEVE TEMPERATURE FROM CACHE...");
          subscriber.onNext(cachedWeather.get());
          subscriber.onCompleted();
        } else {
          Observable.from(restTemplate
              .getForEntity(
                  "http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}",
                  CurrentWeather.class, queryData.getCity(),
                  this.openWeatherCredentials.getApiKey())).subscribe(
              currentWeatherResponseEntity -> {
                weatherCache.addBy(Strategy.NAME, currentWeatherResponseEntity.getBody());
                subscriber.onNext(currentWeatherResponseEntity.getBody());
                subscriber.onCompleted();
              }, e -> {
                LOGGER.error("Error on query city by name", e);
                if (e instanceof HttpClientErrorException) {
                  subscriber.onError(new PlaceNotFoundException());
                } else {
                  subscriber.onError(new OpenWeatherException());
                }
              });
        }
      }
    });
  }

  private Optional<CurrentWeather> checkCacheByCityName(@NonNull String city) {
    return this.weatherCache.getByName(city);
  }

  private Optional<CurrentWeather> checkCacheByCoord(@NonNull Double lat, @NonNull Double lon) {
    return this.weatherCache.getByCoord(lat, lon);
  }

  public Observable<CurrentWeather> fromCache(@NonNull QueryData queryData) {
    if (queryData.isByCoordinate()) {
      LOGGER.info("RETRIEVE DATA TEMPERATURE FROM CACHE...COORDINATE");
      final Optional<CurrentWeather> weather = this.weatherCache
          .getByCoord(queryData.getLat(), queryData.getLon());
      return weather.map(Observable::just)
          .orElseGet(() -> Observable.error(new PlaceNotFoundException()));
    }
    LOGGER.info("RETRIEVE DATA FROM TEMPERATURE CACHE...CITY");
    final Optional<CurrentWeather> weather = this.weatherCache.getByName(queryData.getCity());
    return weather.map(Observable::just)
        .orElseGet(() -> Observable.error(new PlaceNotFoundException()));
  }

}
