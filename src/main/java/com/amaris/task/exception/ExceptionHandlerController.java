package com.amaris.task.exception;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerController {
	private final HttpServletRequest request;

	@ExceptionHandler(value = {Exception.class})
	ResponseEntity<ResponseError> handleException(final Exception ex) {
		log.error("{}", ex);
		
		return new ResponseEntity<ResponseError>( 
			ResponseError.builder()
				.path(this.request.getServletContext().getContextPath() + this.request.getServletPath())
				.errorMessage(ex.getMessage())
				.cause(ex.getCause())
				.timestamp(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date()))
				.build(),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
	
}