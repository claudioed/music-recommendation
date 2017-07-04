package music.recommendation.domain.music;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
public class MusicStyleShufflerTests {

  public MusicStyleShuffler musicStyleShuffler = new MusicStyleShuffler();

  @Test
  public void shouldBeRock(){
    final String style = musicStyleShuffler
        .style(CelsiusTemperature.builder().kelvinValue(285.514).build().getValue());
    assertThat(style).isEqualTo("rock");
  }

  @Test
  public void shouldBePop(){
    final String style = musicStyleShuffler
        .style(CelsiusTemperature.builder().kelvinValue(295.514).build().getValue());
    assertThat(style).isEqualTo("pop");
  }

  @Test
  public void shouldBeElectro(){
    final String style = musicStyleShuffler
        .style(CelsiusTemperature.builder().kelvinValue(310.514).build().getValue());
    assertThat(style).isEqualTo("electro");
  }

  @Test
  public void shouldBeClassical(){
    final String style = musicStyleShuffler
        .style(CelsiusTemperature.builder().kelvinValue(273.00).build().getValue());
    assertThat(style).isEqualTo("classical");
  }


}
