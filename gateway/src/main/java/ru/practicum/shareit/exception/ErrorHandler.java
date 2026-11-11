package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErrorHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getAllErrors().stream()
				.findFirst()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.orElse("что то не так с данными");
		return new ErrorResponse("ошибка валидации", message);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ErrorResponse handleConstraintViolation(final ConstraintViolationException e) {
		return new ErrorResponse("ошибка валидации", e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ErrorResponse handleIllegalArgument(final IllegalArgumentException e) {
		return new ErrorResponse(e.getMessage(), "не корректный запрос");
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ErrorResponse handleNotReadable(final HttpMessageNotReadableException e) {
		return new ErrorResponse("ошибка валидации", "не получилось прочитать данные запроса");
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({
			MissingRequestHeaderException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ErrorResponse handleRequestParameters(final Exception e) {
		return new ErrorResponse("ошибка валидации", e.getMessage());
	}
}
