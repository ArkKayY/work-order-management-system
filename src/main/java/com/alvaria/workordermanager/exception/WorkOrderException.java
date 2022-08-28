package com.alvaria.workordermanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WorkOrderException extends RuntimeException {

	public WorkOrderException(final String message) {
		super(message);
	}

}