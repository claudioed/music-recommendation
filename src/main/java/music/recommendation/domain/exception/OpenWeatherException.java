package music.recommendation.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Open Weather API Exception
 * Created by claudio on 04/07/17.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OpenWeatherException extends RuntimeException {

    public OpenWeatherException(Exception ex) {
        super("Error on contact open weather web api",ex);
    }

}
