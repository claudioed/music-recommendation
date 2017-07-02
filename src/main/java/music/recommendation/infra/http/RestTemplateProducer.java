package music.recommendation.infra.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Configuration
public class RestTemplateProducer {

  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
