package music.recommendation.infra.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author claudioed on 03/07/17. Project music-recommendation
 */
@Configuration
public class JedisProducer {

  @Bean
  public JedisPool jedisPool(){
    return new JedisPool(new JedisPoolConfig(), "localhost");
  }

}
