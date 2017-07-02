package music.recommendation.domain.rest;

import static org.assertj.core.api.Assertions.assertThat;

import music.recommendation.domain.rest.model.QueryData;
import org.junit.Test;

/**
 * @author claudioed on 02/07/17. Project music-recommendation
 */
public class QueryDataTests {

  @Test
  public void shouldBeValidByLatLon(){
    final QueryData queryData = QueryData.builder().lat(111d).lon(888d).build();
    assertThat(queryData.isValid()).isEqualTo(true);
  }

  @Test
  public void shouldBeValidByCity(){
    final QueryData queryData = QueryData.builder().city("London,uk").build();
    assertThat(queryData.isValid()).isEqualTo(true);
  }

  @Test
  public void shouldBeInvalidByOnlyLat(){
    final QueryData queryData = QueryData.builder().lat(1111d).build();
    assertThat(queryData.isValid()).isEqualTo(false);
  }

  @Test
  public void shouldBeInvalidByOnlyLon(){
    final QueryData queryData = QueryData.builder().lon(1111d).build();
    assertThat(queryData.isValid()).isEqualTo(false);
  }

}
