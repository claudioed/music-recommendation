package music.recommendation.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author claudioed on 04/07/17. Project music-recommendation
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidQueryException extends RuntimeException {}
