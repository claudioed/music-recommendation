package music.recommendation.infra.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import music.recommendation.domain.weather.CurrentWeather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author claudioed on 03/07/17. Project music-recommendation
 */
@Service
public class WeatherCache {

  public enum Strategy {

    COORDINATE("coordinate:%s+%s"),
    NAME("name:%s");

    @Getter
    private final String PATTERN;

    Strategy(String pattern) {
      PATTERN = pattern;
    }

  }

  private final JedisPool jedisPool;

  private final ObjectMapper mapper;

  @Autowired
  public WeatherCache(JedisPool jedisPool, ObjectMapper mapper) {
    this.jedisPool = jedisPool;
    this.mapper = mapper;
  }

  @SneakyThrows
  public void addBy(@NonNull Strategy strategy,@NonNull CurrentWeather weather){
    try (Jedis jedis = jedisPool.getResource()) {
      if (Strategy.NAME.equals(strategy)) {
        jedis.set(String.format(strategy.PATTERN, weather.getName()),
            this.mapper.writeValueAsString(weather));
      } else if (Strategy.COORDINATE.equals(strategy)) {
        jedis.set(String
                .format(strategy.PATTERN, weather.getCoord().getLat(), weather.getCoord().getLon()),
            this.mapper.writeValueAsString(weather));
      } else {
        throw new IllegalArgumentException("Invalid strategy");
      }
    }
  }

  @SneakyThrows
  public Optional<CurrentWeather> getByName(@NonNull String name){
    try (Jedis jedis = jedisPool.getResource()) {
      final String data = jedis.get(String.format(Strategy.NAME.PATTERN, name));
      if(Strings.isNullOrEmpty(data)){
        return Optional.empty();
      }
      return Optional.ofNullable(this.mapper.readValue(data, CurrentWeather.class));
    }
  }

  @SneakyThrows
  public Optional<CurrentWeather> getByCoord(@NonNull Double lat,@NonNull Double lon){
    try (Jedis jedis = jedisPool.getResource()) {
      final String data = jedis.get(String.format(Strategy.COORDINATE.PATTERN, lat, lon));
      if(Strings.isNullOrEmpty(data)){
        return Optional.empty();
      }
      return Optional.ofNullable(this.mapper.readValue(data, CurrentWeather.class));
    }
  }

}
