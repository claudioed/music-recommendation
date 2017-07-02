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

//  @GetMapping
//  public String recommend(){
//    final QueryData queryData = QueryData.builder().city("London,uk").build();
//    this.musicRecommendationService.musics(queryData);
//    return "OK";
//  }

  @GetMapping
  public List<String> musics(@RequestParam("lat") Double lat,@RequestParam("lon") Double lon,@RequestParam("city") String city){
    QueryData queryData = QueryData.builder().lat(lat).lon(lon).city(city).build();
    return this.musicRecommendationService.musics(queryData).toBlocking().first();
  }

}
