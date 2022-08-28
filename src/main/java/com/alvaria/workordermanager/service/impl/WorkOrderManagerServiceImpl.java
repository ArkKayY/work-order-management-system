package com.alvaria.workordermanager.service.impl;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.ClassTypeEnum;
import com.alvaria.workordermanager.model.Response;
import com.alvaria.workordermanager.model.WorkOrder;
import com.alvaria.workordermanager.service.WorkOrderManagerService;

import org.springframework.stereotype.Service;

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

@Service
public class WorkOrderManagerServiceImpl implements WorkOrderManagerService {

    private final Queue<Long>                    workOrderQueue         = new PriorityQueue<>(5,
                                                                                              new WorkOrderComparator());
    private final Map<Long, WorkOrder>           workOrderMap           = new HashMap<>();
    private final Map<ClassTypeEnum, List<Long>> classTypeGroupedIdsMap = new HashMap<>();
    private final Map<Long, Double>              rankedMap              = new HashMap<>();

    @Override
    public Response enqueue(final WorkOrder workOrder) {
        final Long workOrderId = workOrder.getId();
        if (workOrderQueue.contains(workOrderId)) {
            throw new WorkOrderException("Id already exists in the queue");
        }
        process(workOrder);
        workOrderQueue.add(workOrderId);
        workOrderMap.put(workOrderId, workOrder);

        Response response = new Response();
        response.setId(workOrder.getId());
        response.setTime(workOrder.getTime());
        return response;
    }

    @Override
    public WorkOrder dequeue() {
        if (workOrderQueue.isEmpty()) {
            throw new WorkOrderException("Queue is empty");
        }
        final Long id = workOrderQueue.poll();
        return workOrderMap.remove(id);
    }

    @Override
    public List<Response> getIds() {
        final List<Response> responseList = new ArrayList<>();
        final Queue<Long> tempQueue = new PriorityQueue<>(workOrderQueue);
        while (!tempQueue.isEmpty()) {
            responseList.add(new Response(tempQueue.poll(), 0.0));
            //            responseList.add(tempQueue.poll());
        }
        return responseList;
    }

    @Override
    public Response delete(final Long id) {
        if (!workOrderQueue.contains(id)) {
            throw new WorkOrderNotFoundException("No such ID found in the queue");
        }
        workOrderQueue.remove(id);
        workOrderMap.remove(id);
    }

    @Override
    public int getPosition(final Long id) {
        int position = 0;
        final Queue<Long> tempQueue = new PriorityQueue<>(workOrderQueue);
        while (!tempQueue.isEmpty()) {
            if (Objects.equals(tempQueue.poll(), id)) {
                return position;
            }
            position++;
        }
        throw new WorkOrderNotFoundException("No such ID found in the queue");
    }

    @Override
    public List<Response> computeWaitingTime(final WorkOrder workOrder) {
        final List<Response> responseList = new ArrayList<>();
        for (WorkOrder wo : workOrderMap.values()) {
            long seconds = ChronoUnit.SECONDS.between(wo.getTime(), workOrder.getTime());
            responseList.add(new Response(wo.getId(), seconds));
        }
        return responseList;
    }

    private void process(final WorkOrder workOrder) {
        final Long id = workOrder.getId();
        final ClassTypeEnum type = computeClassType(id);
        classTypeGroupedIdsMap.computeIfAbsent(type, t -> new ArrayList<>()).add(id);
        final double rank = computeRank(workOrder, type);
        rankedMap.put(id, rank);
    }

    /**
     * Computes the class type based on the value of id
     * @param id The id of work-order
     * @return Class type of id
     */
    private ClassTypeEnum computeClassType(final Long id) {
        if (id % 3 == 0 && id % 5 == 0) { return ClassTypeEnum.MANAGEMENT_OVERRIDE; }
        else if (id % 3 == 0) { return ClassTypeEnum.PRIORITY; }
        else if (id % 5 == 0) { return ClassTypeEnum.VIP; }
        else { return ClassTypeEnum.NORMAL; }
    }

    /**
     * Computes the rank of work-order based on its type
     * @param workOrder The work-order whose rank is to be computed
     * @param type The class type of work-order
     * @return rank of work-order
     */
    private double computeRank(final WorkOrder workOrder, final ClassTypeEnum type) {
        long seconds = ChronoUnit.SECONDS.between(workOrder.getTime(), LocalDateTime.now());
        if (ClassTypeEnum.PRIORITY == type) { return Math.max(3, seconds * Math.log(seconds)); }
        else if (ClassTypeEnum.VIP == type) { return Math.max(4, 2 * seconds * Math.log(seconds)); }
        else { return seconds; }
    }

    /**
     * Retrieves the class type of work-order id
     * @param id The id of work-order
     * @return Class type of id
     */
    private ClassTypeEnum getClassTypeById(final Long id) {
        for (ClassTypeEnum classType : classTypeGroupedIdsMap.keySet()) {
            if (classTypeGroupedIdsMap.get(classType).contains(id)) {
                return classType;
            }
        }
        return null;
    }

    /**
     * Retrieves the rank of work-order id
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
            }
            else if (ClassTypeEnum.MANAGEMENT_OVERRIDE == type1) { return -1; }
            else if (ClassTypeEnum.MANAGEMENT_OVERRIDE == type2) { return 1; }
            else { return rank2.compareTo(rank1); }
        }

    }

}
