package music.recommendation.domain.rest;

import java.util.List;
import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.service.MusicRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
@RestController
@RequestMapping("/api/recommendation")
public class MusicRecommendationResource {

  private final MusicRecommendation musicRecommendation;

  @Autowired
  public MusicRecommendationResource(MusicRecommendation musicRecommendation) {
    this.musicRecommendation = musicRecommendation;
  }

  @GetMapping
  public DeferredResult<List<String>> recommendation(@RequestParam(value = "city",required = false) String city,@RequestParam(value = "lat",required = false) Double lat,@RequestParam(value = "lon",required = false) Double lon){
    QueryData queryData = QueryData.builder().lat(lat).lon(lon).city(city).build();
    final Observable<List<String>> observable = this.musicRecommendation.musics(queryData);
    DeferredResult<List<String>> deffered = new DeferredResult<>(90L);
    observable.subscribe(deffered::setResult, deffered::setErrorResult);
    return deffered;
  }

}
