package com.alvaria.workordermanager.model;

import java.time.LocalDateTime;

/**
 * Response model for Work-Orders
 */
public class Response {

    private Long          id;
    private LocalDateTime time;
    private String        message;
    private int           position;
    private double        waitingTime;

    public Response(final String message) { this.message = message; }

    public Response(final int position) {
        this.position = position;
    }

    public Response(final Long id, final double waitingTime) {
        this.id = id;
        this.waitingTime = waitingTime;
    }

    public Response() {
    }

    public Long getId() { return id; }

    public void setId(final Long id) { this.id = id; }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(final LocalDateTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(final double waitingTime) {
        this.waitingTime = waitingTime;
    }

}
