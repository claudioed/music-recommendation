package music.recommendation.infra.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import music.recommendation.domain.weather.CurrentWeather;
import music.recommendation.infra.redis.WeatherCache.Strategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author claudioed on 03/07/17. Project music-recommendation
 */
@Service
public class MusicCache {

  private final JedisPool jedisPool;

  private final ObjectMapper mapper;

  private static final String PATTERN = "style:%s";

  @Autowired
  public MusicCache(JedisPool jedisPool, ObjectMapper mapper) {
    this.jedisPool = jedisPool;
    this.mapper = mapper;
  }

  @SneakyThrows
  public void addMusics(@NonNull String style,@NonNull List<String> musics){
    try (Jedis jedis = jedisPool.getResource()) {
        jedis.sadd(String.format(PATTERN,style),musics.toArray(new String[musics.size()]));
      }
  }

  @SneakyThrows
  public Optional<List<String>> getMusics(@NonNull String style){
    try (Jedis jedis = jedisPool.getResource()) {
      final Set<String> musics = jedis.smembers(String.format(PATTERN, style));
      return Optional.of(new ArrayList<>(musics));
    }
  }

}
