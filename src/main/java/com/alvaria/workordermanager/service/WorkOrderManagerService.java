package com.alvaria.workordermanager.service;

import java.util.List;

import com.alvaria.workordermanager.model.WorkOrderRequest;
import com.alvaria.workordermanager.model.WorkOrderResponse;

public interface WorkOrderManagerService {

	/**
	 * Inserts a work-order in the priority queue.
	 * 
	 * @param workOrderRequest work-order to insert
	 * @return Inserted work-order
	 */
	WorkOrderResponse enqueue(WorkOrderRequest workOrderRequest);

	/**
	 * Retrieves and removes the top work-order from the priority queue.
	 * 
	 * @return The highest ranked work-order
	 */
	WorkOrderResponse dequeue();

	/**
	 * Retrieves all the ID's present in the priority queue.
	 * 
	 * @return List of all ID's
	 */
	List<WorkOrderResponse> getIds();

	/**
	 * Removes a specific ID from the priority queue.
	 * 
	 * @param id The id of work-order
	 * @return The removed id
	 */
	WorkOrderResponse delete(Long id);

	/**
	 * Retrieves the position of given ID from the priority queue.
	 * 
	 * @param id The id of work-order
	 * @return The position of id
	 */
	WorkOrderResponse getPosition(Long id);

	/**
	 * Computes the average waiting time of all the ID's in the priority queue.
	 * 
	 * @param workOrderRequest work-order with current time
	 * @return List pf all ID's with their average waiting time
	 */
	List<WorkOrderResponse> computeWaitingTime(WorkOrderRequest workOrderRequest);

}
