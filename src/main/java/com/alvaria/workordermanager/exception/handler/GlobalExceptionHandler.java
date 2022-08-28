package com.alvaria.workordermanager.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(WorkOrderException.class)
	public ResponseEntity<ErrorDetails> workOrderExceptionHandler(final WorkOrderException workOrderException,
			final WebRequest webRequest) {
		final ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(),
				workOrderException.getMessage(), webRequest.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(WorkOrderNotFoundException.class)
	public ResponseEntity<ErrorDetails> workOrderNotFoundExceptionHandler(
			final WorkOrderNotFoundException workOrderNotFoundException,
			final WebRequest webRequest) {
		final ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(),
				workOrderNotFoundException.getMessage(), webRequest.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

}