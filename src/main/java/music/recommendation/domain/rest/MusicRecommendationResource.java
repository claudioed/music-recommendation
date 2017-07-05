package music.recommendation.domain.rest;

import java.util.List;
import music.recommendation.domain.exception.InvalidQueryException;
import music.recommendation.domain.rest.model.QueryData;
import music.recommendation.domain.service.MusicRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
  public DeferredResult<ResponseEntity<List<String>>> recommendation(
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "lat", required = false) Double lat,
      @RequestParam(value = "lon", required = false) Double lon) {
    QueryData queryData = QueryData.builder().lat(lat).lon(lon).city(city).build();
    if(queryData.isValid()){
      final Observable<List<String>> observable = this.musicRecommendation.musics(queryData);
      DeferredResult<ResponseEntity<List<String>>> result = new DeferredResult<>(90L);
      observable.subscribe(musics -> result.setResult(ResponseEntity.ok(musics)), result::setErrorResult);
      return result;
    }else{
      throw new InvalidQueryException();
    }
  }

}
