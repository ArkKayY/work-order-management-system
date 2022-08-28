package com.alvaria.workordermanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WorkOrderNotFoundException extends RuntimeException {

	public WorkOrderNotFoundException(final String message) {
		super(message);
	}

}