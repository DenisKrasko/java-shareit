package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ErrorResponse handleNotFound(final NotFoundException e) {
		return new ErrorResponse("Объект не найден.", e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ValidationException.class)
	public ErrorResponse handleValidation(final ValidationException e) {
		return new ErrorResponse("Ошибка валидации.", e.getMessage());
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DuplicateEmailException.class)
	public ErrorResponse handleDuplicateEmail(final DuplicateEmailException e) {
		return new ErrorResponse("Ошибка. Такой email уже используется", e.getMessage());
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessException.class)
	public ErrorResponse handleAccess(final AccessException e) {
		return new ErrorResponse("Ошибка доступа", e.getMessage());
	}
}
