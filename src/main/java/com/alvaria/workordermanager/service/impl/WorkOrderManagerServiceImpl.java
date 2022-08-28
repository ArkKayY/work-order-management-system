package com.alvaria.workordermanager.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.ClassTypeEnum;
import com.alvaria.workordermanager.model.WorkOrderRequest;
import com.alvaria.workordermanager.model.WorkOrderResponse;
import com.alvaria.workordermanager.service.WorkOrderManagerService;

@Service
public class WorkOrderManagerServiceImpl implements WorkOrderManagerService {

	private final Queue<Long> workOrderQueue = new PriorityQueue<>(5, new WorkOrderComparator());
	private final Map<Long, WorkOrderRequest> workOrderMap = new HashMap<>();
	private final Map<ClassTypeEnum, List<Long>> classTypeGroupedIdsMap = new HashMap<>();
	private final Map<Long, Double> rankedMap = new HashMap<>();

	@Override
	public WorkOrderResponse enqueue(final WorkOrderRequest workOrderRequest) {
		final Long workOrderId = workOrderRequest.getId();
		if (workOrderQueue.contains(workOrderId)) {
			throw new WorkOrderException("Id already exists in the queue");
		}

		process(workOrderRequest);
		workOrderQueue.add(workOrderId);
		workOrderMap.put(workOrderId, workOrderRequest);

		final WorkOrderResponse workOrderResponse = WorkOrderResponse.builder()
				.id(workOrderRequest.getId())
				.time(workOrderRequest.getTime())
				.build();
		return workOrderResponse;
	}

	@Override
	public WorkOrderResponse dequeue() {
		if (workOrderQueue.isEmpty()) {
			throw new WorkOrderException("Queue is empty");
		}

		final Long id = workOrderQueue.poll();
		final WorkOrderRequest workOrder = workOrderMap.remove(id);

		final WorkOrderResponse workOrderResponse = WorkOrderResponse.builder()
				.id(workOrder.getId())
				.time(workOrder.getTime())
				.build();
		return workOrderResponse;
	}

	@Override
	public List<WorkOrderResponse> getIds() {
		final List<WorkOrderResponse> responseList = new ArrayList<>();
		final Queue<Long> tempQueue = new PriorityQueue<>(workOrderQueue);

		while (!tempQueue.isEmpty()) {
			final WorkOrderResponse response = WorkOrderResponse.builder()
					.id(tempQueue.poll())
					.build();
			responseList.add(response);
		}
		return responseList;
	}

	@Override
	public WorkOrderResponse delete(final Long id) {
		if (!workOrderQueue.contains(id)) {
			throw new WorkOrderNotFoundException("No such ID found in the queue");
		}
		workOrderQueue.remove(id);
		final WorkOrderRequest workOrder = workOrderMap.remove(id);

		final WorkOrderResponse workOrderResponse = WorkOrderResponse.builder()
				.id(workOrder.getId())
				.time(workOrder.getTime())
				.build();
		return workOrderResponse;
	}

	@Override
	public WorkOrderResponse getPosition(final Long id) {
		int position = 0;
		final Queue<Long> tempQueue = new PriorityQueue<>(workOrderQueue);
		while (!tempQueue.isEmpty()) {
			if (Objects.equals(tempQueue.poll(), id)) {
				final WorkOrderResponse workOrderResponse = WorkOrderResponse.builder()
						.position(position)
						.build();
				return workOrderResponse;
			}
			position++;
		}
		throw new WorkOrderNotFoundException("No such ID found in the queue");
	}

	@Override
	public List<WorkOrderResponse> computeWaitingTime(final WorkOrderRequest workOrderRequest) {
		if (workOrderQueue.isEmpty()) {
			throw new WorkOrderException("Queue is empty");
		}

		final List<WorkOrderResponse> responseList = new ArrayList<>();
		for (final WorkOrderRequest wo : workOrderMap.values()) {
			final long seconds = ChronoUnit.SECONDS.between(wo.getTime(), workOrderRequest.getTime());
			final WorkOrderResponse response = WorkOrderResponse.builder()
					.id(wo.getId())
					.waitingTime(seconds)
					.build();
			responseList.add(response);
		}
		return responseList;
	}

	private void process(final WorkOrderRequest workOrderRequest) {
		final Long id = workOrderRequest.getId();
		final ClassTypeEnum type = computeClassType(id);
		classTypeGroupedIdsMap.computeIfAbsent(type, t -> new ArrayList<>())
				.add(id);
		final double rank = computeRank(workOrderRequest, type);
		rankedMap.put(id, rank);
	}

	/**
	 * Computes the class type based on the value of id
	 * 
	 * @param id The id of work-order
	 * @return Class type of id
	 */
	private ClassTypeEnum computeClassType(final Long id) {
		if (id % 3 == 0 && id % 5 == 0) {
			return ClassTypeEnum.MANAGEMENT_OVERRIDE;
		} else if (id % 3 == 0) {
			return ClassTypeEnum.PRIORITY;
		} else if (id % 5 == 0) {
			return ClassTypeEnum.VIP;
		} else {
			return ClassTypeEnum.NORMAL;
		}
	}

	/**
	 * Computes the rank of work-order based on its type
	 * 
	 * @param workOrderRequest The work-order whose rank is to be computed
	 * @param type             The class type of work-order
	 * @return rank of work-order
	 */
	private double computeRank(final WorkOrderRequest workOrderRequest, final ClassTypeEnum type) {
		final long seconds = ChronoUnit.SECONDS.between(workOrderRequest.getTime(), LocalDateTime.now());
		if (ClassTypeEnum.PRIORITY == type) {
			return Math.max(3, seconds * Math.log(seconds));
		} else if (ClassTypeEnum.VIP == type) {
			return Math.max(4, 2 * seconds * Math.log(seconds));
		} else {
			return seconds;
		}
	}

	/**
	 * Retrieves the class type of work-order id
	 * 
	 * @param id The id of work-order
	 * @return Class type of id
	 */
	private ClassTypeEnum getClassTypeById(final Long id) {
		for (final ClassTypeEnum classType : classTypeGroupedIdsMap.keySet()) {
			final List<Long> idList = classTypeGroupedIdsMap.get(classType);
			if (idList.contains(id)) {
				return classType;
			}
		}
		return null;
	}

	/**
	 * Retrieves the rank of work-order id
	 * 
	 * @param id The id of work-order
	 * @return The rank of id
	 */
	private Double getRankById(final Long id) {
		return rankedMap.get(id);
	}

	/**
	 * Custom comparator to order elements in the priority queue
	 */
	class WorkOrderComparator implements Comparator<Long> {

		/**
		 * Ranks all Management Override type of work-orders on top of the queue.
		 *
		 * Ranks the remaining type of work-orders based on their rank.
		 *
		 * @param id1 the first object to be compared.
		 * @param id2 the second object to be compared.
		 * @return int value
		 */
		@Override
		public int compare(final Long id1, final Long id2) {
			final ClassTypeEnum type1 = getClassTypeById(id1);
			final ClassTypeEnum type2 = getClassTypeById(id2);
			final Double rank1 = getRankById(id1);
			final Double rank2 = getRankById(id2);

			if (ClassTypeEnum.MANAGEMENT_OVERRIDE == type1 && ClassTypeEnum.MANAGEMENT_OVERRIDE == type2) {
				return rank2.compareTo(rank1);
			} else if (ClassTypeEnum.MANAGEMENT_OVERRIDE == type1) {
				return -1;
			} else if (ClassTypeEnum.MANAGEMENT_OVERRIDE == type2) {
				return 1;
			} else {
				return rank2.compareTo(rank1);
			}
		}

	}

}
