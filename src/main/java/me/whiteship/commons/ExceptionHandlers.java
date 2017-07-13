package me.whiteship.commons;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import me.whiteship.accounts.UserDuplicatedException;

@ControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(UserDuplicatedException.class)
	public ResponseEntity handleuserDuplicatedException (UserDuplicatedException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[ " + e.getUsername() + " ] 중복된 username 입니다.");
		errorResponse.setCode("duplicated.username.exception");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
