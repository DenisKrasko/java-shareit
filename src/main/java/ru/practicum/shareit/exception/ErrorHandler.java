package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(NotFoundException.class)
	public ErrorResponse handleNotFound(final NotFoundException e) {
		return new ErrorResponse("Объект не найден.", e.getMessage());
	}
}
