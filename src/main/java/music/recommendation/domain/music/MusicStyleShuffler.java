package music.recommendation.domain.music;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
public class MusicStyleShuffler {

  public String style(Double temperature) {
    if (temperature >= 15 && temperature <= 30) {
      return "pop";
    } else if (temperature >= 10 && temperature <= 14.99) {
      return "rock";
    } else if (temperature >= 30) {
      return "party";
    } else {
      return "classical";
    }
  }

}
