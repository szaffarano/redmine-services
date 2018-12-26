package redmine.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class Exceptions {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    static class ForbiddenException extends RuntimeException {

        public ForbiddenException() {
            super();
        }

        public ForbiddenException(String msg) {
            super(msg);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotConfiguredException extends RuntimeException {

        public NotConfiguredException() {
            super();
        }

        public NotConfiguredException(String msg) {
            super(msg);
        }
    }
}
