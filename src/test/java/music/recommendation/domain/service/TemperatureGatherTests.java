package music.recommendation.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.weather.CurrentWeather;
import music.recommendation.domain.weather.Main;
import music.recommendation.infra.weather.OpenWeatherCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @author claudioed on 03/07/17. Project music-recommendation
 */
@RunWith(MockitoJUnitRunner.class)
public class TemperatureGatherTests {

  @Mock
  private OpenWeatherCredentials openWeatherCredentials;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private TemperatureGather temperatureGather;

  private CurrentWeather expected;

  @Before
  public void setupData(){
    expected = new CurrentWeather();
    expected.setName("Campinas");
    final Main main = new Main();
    main.setTemp(10d);
    expected.setMain(main);
  }

  @Test
  public void shouldBeNameEquals() {
    final QueryData query = QueryData.builder().city("Campinas").build();
    when(this.openWeatherCredentials.getApiKey()).thenReturn("XYZ");
    when(restTemplate
        .getForObject("http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}",
            CurrentWeather.class, query.getCity(), this.openWeatherCredentials.getApiKey())).thenReturn(expected);
    this.temperatureGather.weatherData(query).subscribe(value -> assertThat(value.getName()).isEqualTo(expected.getName()));
  }

  @Test
  public void shouldBeSameTemperature() {
    final QueryData query = QueryData.builder().city("Campinas").build();
    when(this.openWeatherCredentials.getApiKey()).thenReturn("XYZ");
    when(restTemplate
        .getForObject("http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}",
            CurrentWeather.class, query.getCity(), this.openWeatherCredentials.getApiKey())).thenReturn(expected);
    this.temperatureGather.weatherData(query).subscribe(value -> assertThat(value.getMain().getTemp()).isEqualTo(expected.getMain().getTemp()));
  }

}
