package music.recommendation.infra.credential;

import music.recommendation.infra.spotify.SpotifyCredentials;
import music.recommendation.infra.weather.OpenWeatherCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@Configuration
public class CredentialsProducer {

  @Value("${spotify.client.id}")
  private String spotifyClientId;

  @Value("${spotify.client.secret}")
  private String spotifyClientSecret;

  @Value("${openweather.apikey}")
  private String openWeatherApiKey;

  @Bean
  public SpotifyCredentials spotifyCredentials(){
    return SpotifyCredentials.builder().clientId(this.spotifyClientId).clientSecret(this.spotifyClientSecret).build();
  }

  @Bean
  public OpenWeatherCredentials openWeatherCredentials(){
    return OpenWeatherCredentials.builder().apiKey(this.openWeatherApiKey).build();
  }

}
