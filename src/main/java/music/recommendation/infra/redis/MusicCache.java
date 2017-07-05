package music.recommendation.infra.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.SneakyThrows;
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

  private static final String PATTERN = "style:%s";

  private static final Long MUSIC_TTL = 7200L;

  @Autowired
  public MusicCache(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

  @SneakyThrows
  public void addMusics(@NonNull String style, @NonNull List<String> musics) {
    try (Jedis jedis = jedisPool.getResource()) {
      final String key = String.format(PATTERN, style);
      jedis.sadd(key, musics.toArray(new String[musics.size()]));
      jedis.expireAt(key,MUSIC_TTL);
    }
  }

  @SneakyThrows
  public Optional<List<String>> getMusics(@NonNull String style) {
    try (Jedis jedis = jedisPool.getResource()) {
      final Set<String> musics = jedis.smembers(String.format(PATTERN, style));
      return Optional.of(new ArrayList<>(musics));
    }
  }

}
