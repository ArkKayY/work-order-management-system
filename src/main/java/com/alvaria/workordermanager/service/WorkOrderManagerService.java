package com.alvaria.workordermanager.service;

import com.alvaria.workordermanager.model.Response;
import com.alvaria.workordermanager.model.WorkOrder;

import java.util.List;

public interface WorkOrderManagerService {

    /**
     * Inserts a work-order in the priority queue.
     * @param workOrder work-order to insert
     * @return Inserted work-order
     */
    Response enqueue(WorkOrder workOrder);

    /**
     * Retrieves and removes the top work-order from the priority queue.
     * @return The highest ranked work-order
     */
    WorkOrder dequeue();

    /**
     * Retrieves all the ID's present in the priority queue.
     * @return List of all ID's
     */
    List<Response> getIds();

    /**
     * Removes a specific ID from the priority queue.
     * @param id The id of work-order
     * @return The removed id
     */
    Response delete(Long id);

    /**
     * Retrieves the position of given ID from the priority queue.
     * @param id The id of work-order
     * @return The position of id
     */
    int getPosition(Long id);

    /**
     * Computes the average waiting time of all the ID's in the priority queue.
     * @param workOrder work-order with current time
     * @return List pf all ID's with their average waiting time
     */
    List<Response> computeWaitingTime(WorkOrder workOrder);

}
