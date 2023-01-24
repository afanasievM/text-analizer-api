package com.ligazakon.test.ligazakon.handlers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "No content.", content = @Content)
    public ResponseEntity handleTeacherConflictException(Exception ex) {
        logger.error(stackTraceToString(ex.getStackTrace()));
        return new ResponseEntity("No content", HttpStatus.NO_CONTENT);
    }

    private String stackTraceToString(StackTraceElement[] stackTraceElements) {
        StringBuilder result = new StringBuilder();
        Arrays.stream(stackTraceElements).forEach(stack -> result.append(stack.toString() + "\n"));
        return result.toString();
    }
}
