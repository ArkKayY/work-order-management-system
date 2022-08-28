package com.alvaria.workordermanager.exception.handler;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.ErrorDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WorkOrderException.class)
    public ResponseEntity<ErrorDetails> workOrderExceptionHandler(WorkOrderException workOrderException,
                                                                  WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(workOrderException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WorkOrderNotFoundException.class)
    public ResponseEntity<ErrorDetails> workOrderNotFoundExceptionHandler(WorkOrderNotFoundException workOrderNotFoundException,
                                                                          WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(workOrderNotFoundException.getMessage(),
                                                     webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

}
