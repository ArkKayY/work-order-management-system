package com.alvaria.workordermanager.service;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.Response;
import com.alvaria.workordermanager.model.WorkOrder;
import com.alvaria.workordermanager.service.impl.WorkOrderManagerServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class WorkOrderManagerServiceTest {

    @InjectMocks
    private WorkOrderManagerServiceImpl workOrderManagerService;

    @Test
    public void testEnqueue() {
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());
        this.workOrderManagerService.enqueue(workOrder);
        assertThrows(WorkOrderException.class, () -> this.workOrderManagerService.enqueue(workOrder));
    }

    @Test
    public void testDequeue_Success() {
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());
        this.workOrderManagerService.enqueue(workOrder);
        assertEquals(this.workOrderManagerService.dequeue(), workOrder);
    }

    @Test
    public void testDequeue_Empty() {
        assertThrows(WorkOrderException.class, () -> this.workOrderManagerService.dequeue());
    }

    @Test
    public void testGetIds() {
        this.workOrderManagerService.enqueue(new WorkOrder(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(30L, LocalDateTime.of(2022, 8, 24, 19, 50, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(16L, LocalDateTime.of(2022, 8, 24, 18, 22, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(25L, LocalDateTime.of(2022, 8, 24, 18, 23, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(45L, LocalDateTime.of(2022, 8, 24, 18, 50, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(15L, LocalDateTime.of(2022, 8, 24, 18, 21, 50)));

        final List<Long> expected = List.of(15L, 45L, 30L, 25L, 3L, 16L);

        assertEquals(this.workOrderManagerService.getIds(), expected);
    }

    @Test
    public void testDelete() {
        assertThrows(WorkOrderNotFoundException.class, () -> this.workOrderManagerService.delete(1L));
    }

    @Test
    public void testGetPosition_Success() {
        final List<Long> expected = List.of(15L, 45L, 30L, 25L, 3L, 16L);

        this.workOrderManagerService.enqueue(new WorkOrder(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(30L, LocalDateTime.of(2022, 8, 24, 19, 50, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(16L, LocalDateTime.of(2022, 8, 24, 18, 22, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(25L, LocalDateTime.of(2022, 8, 24, 18, 23, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(45L, LocalDateTime.of(2022, 8, 24, 18, 50, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(15L, LocalDateTime.of(2022, 8, 24, 18, 21, 50)));

        assertEquals(this.workOrderManagerService.getPosition(30L), expected.indexOf(30L));
    }

    @Test
    public void testGetPosition_Failure() {
        this.workOrderManagerService.enqueue(new WorkOrder(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));

        assertThrows(WorkOrderNotFoundException.class, () -> this.workOrderManagerService.getPosition(30L));
    }

    @Test
    public void testComputeWaitingTime() {
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());
        final List<Long> idList = List.of(3L, 30L, 16L, 25L, 45L, 15L);

        this.workOrderManagerService.enqueue(new WorkOrder(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(30L, LocalDateTime.of(2022, 8, 24, 19, 50, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(16L, LocalDateTime.of(2022, 8, 24, 18, 22, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(25L, LocalDateTime.of(2022, 8, 24, 18, 23, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(45L, LocalDateTime.of(2022, 8, 24, 18, 50, 50)));
        this.workOrderManagerService.enqueue(new WorkOrder(15L, LocalDateTime.of(2022, 8, 24, 18, 21, 50)));

        final List<Response> responseList = this.workOrderManagerService.computeWaitingTime(workOrder);
        assertEquals(responseList.size(), idList.size());
        assertThat(responseList.stream().map(Response::getId).collect(Collectors.toList())).containsAll(idList);
    }

}
