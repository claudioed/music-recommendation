package music.recommendation.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Configuration
public class RestTemplateProducer {


  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public AsyncRestTemplate asyncRestTemplate() {
    return new AsyncRestTemplate();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
