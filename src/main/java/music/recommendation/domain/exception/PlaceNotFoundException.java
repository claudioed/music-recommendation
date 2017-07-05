package music.recommendation.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Open Weather API Not Found Exception
 * Created by claudio on 04/07/17.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlaceNotFoundException extends RuntimeException {

    public PlaceNotFoundException() {
        super("Place not found");
    }

}
