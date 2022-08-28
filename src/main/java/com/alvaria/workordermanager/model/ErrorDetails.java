package com.alvaria.workordermanager.model;

/**
 * Model to display error details during exception
 */
public class ErrorDetails {

    private String errorMessage;
    private String errorDetails;

    public ErrorDetails(final String errorMessage, final String errorDetails) {
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }

    public String getErrorMessage() { return errorMessage; }

    public String getErrorDetails() { return errorDetails; }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrorDetails(final String errorDetails) {
        this.errorDetails = errorDetails;
    }

}
