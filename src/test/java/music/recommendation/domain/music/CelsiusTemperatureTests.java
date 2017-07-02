package music.recommendation.domain.music;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
public class CelsiusTemperatureTests {

  @Test
  public void shouldBeZeroDegrees(){
    final CelsiusTemperature celsiusTemperature = CelsiusTemperature.builder().kelvinValue(273d).build();
    assertThat(celsiusTemperature.getValue()).isEqualTo(0d);

  }

  @Test
  public void shouldBeTwentyDegrees(){
    final CelsiusTemperature celsiusTemperature = CelsiusTemperature.builder().kelvinValue(293d).build();
    assertThat(celsiusTemperature.getValue()).isEqualTo(20d);
  }

  @Test
  public void shouldBeAboveZeroDegrees(){
    final CelsiusTemperature celsiusTemperature = CelsiusTemperature.builder().kelvinValue(310d).build();
    assertThat(celsiusTemperature.getValue()).isGreaterThan(0d);
  }

  @Test
  public void shouldBeBelowZeroDegrees(){
    final CelsiusTemperature celsiusTemperature = CelsiusTemperature.builder().kelvinValue(250d).build();
    assertThat(celsiusTemperature.getValue()).isLessThan(0d);
  }

}
