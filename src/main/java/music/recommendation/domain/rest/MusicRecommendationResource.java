package music.recommendation.domain.rest;

import java.util.List;
import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.service.MusicRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@RestController
@RequestMapping("/api/recommendation")
public class MusicRecommendationResource {

  private final MusicRecommendationService musicRecommendationService;

  @Autowired
  public MusicRecommendationResource(MusicRecommendationService musicRecommendationService) {
    this.musicRecommendationService = musicRecommendationService;
  }

  @GetMapping
  public List<String> musics(@RequestParam("city") String city){
    QueryData queryData = QueryData.builder().lat(null).lon(null).city(city).build();
    return this.musicRecommendationService.musics(queryData).toBlocking().first();
  }

}
