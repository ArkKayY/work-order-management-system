package com.alvaria.workordermanager.model;

import java.time.LocalDateTime;

/**
 * Request model for Work-Orders
 */
public class WorkOrder {

    private Long id;

    private LocalDateTime time;

    public WorkOrder(final Long id, final LocalDateTime time) {
        this.id = id;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setTime(final LocalDateTime time) {
        this.time = time;
    }

}
