package dz.eadn.sig.controller.common;

import java.util.Date;

import dz.eadn.sig.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandletController {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest req) {
		ExceptionResponce ced = new ExceptionResponce(new Date(), ex.getMessage(), req.getDescription(false));

		return new ResponseEntity<Object>(ced, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(EntityAlreadyExistsException.class)
	public ResponseEntity<Object> handleEntityAlreadyExistException(EntityAlreadyExistsException ex, WebRequest req) {
		ExceptionResponce ced = new ExceptionResponce(new Date(), ex.getMessage(), req.getDescription(false));

		return new ResponseEntity<Object>(ced, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest req) {
		ExceptionResponce ced = new ExceptionResponce(new Date(), ex.getMessage(), req.getDescription(false));

		return new ResponseEntity<Object>(ced, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessNotPermittedException.class)
	public ResponseEntity<Object> handleAccessNotPermittedException(AccessNotPermittedException ex, WebRequest req) {
		ExceptionResponce ced = new ExceptionResponce(new Date(), ex.getMessage(), req.getDescription(false));

		return new ResponseEntity<Object>(ced, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<Object> handleCustomException(GlobalException ex, WebRequest req) {
		ExceptionResponce ced = new ExceptionResponce(new Date(), ex.getMessage(), req.getDescription(false));

		return new ResponseEntity<Object>(ced, HttpStatus.BAD_REQUEST);
	}

}
