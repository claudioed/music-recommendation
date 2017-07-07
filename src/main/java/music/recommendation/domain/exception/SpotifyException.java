package music.recommendation.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Spotify Exception
 * Created by claudio on 04/07/17.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpotifyException extends RuntimeException {

    public SpotifyException(Throwable ex) {
        super("Error on contact spotify web api",ex);
    }

}
