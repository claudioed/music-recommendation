package music.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
public class MusicRecommendationApplication {

  public static void main(String[] args) {
    SpringApplication.run(MusicRecommendationApplication.class, args);
  }

}