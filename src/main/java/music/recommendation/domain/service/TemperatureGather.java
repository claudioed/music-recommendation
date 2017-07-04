package music.recommendation.domain.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import java.util.Optional;

import lombok.NonNull;
import music.recommendation.domain.exception.OpenWeatherException;
import music.recommendation.domain.exception.SpotifyException;
import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.weather.CurrentWeather;
import music.recommendation.infra.redis.WeatherCache;
import music.recommendation.infra.redis.WeatherCache.Strategy;
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

    private final RestTemplate restTemplate;

    private final OpenWeatherCredentials openWeatherCredentials;

    private final WeatherCache weatherCache;

    @Autowired
    public TemperatureGather(RestTemplate restTemplate,
                             OpenWeatherCredentials openWeatherCredentials,
                             WeatherCache weatherCache) {
        this.restTemplate = restTemplate;
        this.openWeatherCredentials = openWeatherCredentials;
        this.weatherCache = weatherCache;
    }

    @HystrixCommand(fallbackMethod = "fromCache", commandKey = "weatherdata", groupKey = "weather", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "100"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
    }, threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "5"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")})
    public Observable<CurrentWeather> weatherData(@NonNull QueryData queryData) {
        if (queryData.isByCoordinate()) {
            return Observable.create(subscriber -> {
                try {
                    final CurrentWeather data = restTemplate.getForObject(
                            "http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={appid}",
                            CurrentWeather.class, queryData.getLat(), queryData.getLon(),
                            this.openWeatherCredentials.getApiKey());
                    LOGGER.info(String.format("[BY COORDINATE] Current temperature in %s is %s (Kelvin) ",
                            data.getName(), data.getMain().getTemp()));
                    weatherCache.addBy(Strategy.COORDINATE, data);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(data);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    LOGGER.error("Error on query city by coordinate", e);
                    Observable.error(new OpenWeatherException(e));
                }
            });
        }
        return Observable.create(subscriber -> {
            try {
                final CurrentWeather data = restTemplate
                        .getForObject("http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}",
                                CurrentWeather.class, queryData.getCity(), this.openWeatherCredentials.getApiKey());
                LOGGER.info(String.format("[BY CITY NAME] Current temperature in %s is %s (Kelvin) ",
                        data.getName(), data.getMain().getTemp()));
                weatherCache.addBy(Strategy.NAME, data);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                LOGGER.error("Error on query city by name", e);
                Observable.error(new OpenWeatherException(e));
            }
        });
    }

    public Observable<CurrentWeather> fromCache(@NonNull QueryData queryData) {
        if (queryData.isByCoordinate()) {
            LOGGER.info("RETRIEVE DATA TEMPERATURE FROM CACHE...COORDINATE");
            final Optional<CurrentWeather> weather = this.weatherCache
                    .getByCoord(queryData.getLat(), queryData.getLon());
            return Observable.just(weather.get());
        }
        LOGGER.info("RETRIEVE DATA FROM TEMPERATURE CACHE...CITY");
        final Optional<CurrentWeather> weather = this.weatherCache.getByName(queryData.getCity());
        return Observable.just(weather.get());
    }

}
