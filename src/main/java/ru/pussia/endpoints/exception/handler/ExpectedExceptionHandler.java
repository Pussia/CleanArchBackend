package ru.pussia.endpoints.exception.handler;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.pussia.endpoints.exception.BadRequestResponse;
import ru.pussia.endpoints.exception.OutOfBoundReadingException;
import ru.pussia.endpoints.exception.UnknownKeywordException;

import java.io.IOException;

@ControllerAdvice
public class ExpectedExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ExpectedExceptionHandler.class);

    @ExceptionHandler(OutOfBoundReadingException.class)
    public ResponseEntity<BadRequestResponse> handleOutOfBoundReadingException(OutOfBoundReadingException e) {
        logger.error("Reading file failed", e);

        BadRequestResponse badRequestResponse = new BadRequestResponse(e.getMessage());

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownKeywordException.class)
    public ResponseEntity<BadRequestResponse> handleUnknownKeywordException(UnknownKeywordException e) {
        logger.error("Dividing file failed", e);

        BadRequestResponse badRequestResponse = new BadRequestResponse(e.getMessage());

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GitAPIException.class)
    public ResponseEntity<BadRequestResponse> handleGitAPIException(GitAPIException e) {
        logger.error("Cloning project failed", e);

        BadRequestResponse badRequestResponse = new BadRequestResponse(e.getMessage());

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<BadRequestResponse> handleIOException(IOException e) {
        logger.error("Unknown IOExpcetion throwed", e);

        BadRequestResponse badRequestResponse = new BadRequestResponse(e.getMessage());

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }
}
