package com.alvaria.workordermanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.WorkOrderRequest;
import com.alvaria.workordermanager.model.WorkOrderResponse;
import com.alvaria.workordermanager.service.impl.WorkOrderManagerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class WorkOrderManagerServiceTest {

	@InjectMocks
	private WorkOrderManagerServiceImpl workOrderManagerService;

	@Test
	public void testEnqueue_Success() {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());
		final WorkOrderResponse response = this.workOrderManagerService.enqueue(workOrderRequest);
		assertEquals(response.getId(), workOrderRequest.getId());
	}

	@Test
	public void testEnqueue_Failure() {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());
		this.workOrderManagerService.enqueue(workOrderRequest);
		assertThrows(WorkOrderException.class, () -> this.workOrderManagerService.enqueue(workOrderRequest));
	}

	@Test
	public void testDequeue_Success() {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());
		this.workOrderManagerService.enqueue(workOrderRequest);
		final WorkOrderResponse response = this.workOrderManagerService.dequeue();
		assertEquals(response.getId(), workOrderRequest.getId());
	}

	@Test
	public void testDequeue_Failure() {
		assertThrows(WorkOrderException.class, () -> this.workOrderManagerService.dequeue());
	}

	@Test
	public void testGetIds() {
		this.workOrderManagerService.enqueue(new WorkOrderRequest(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(30L, LocalDateTime.of(2022, 8, 24, 19, 50, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(16L, LocalDateTime.of(2022, 8, 24, 18, 22, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(25L, LocalDateTime.of(2022, 8, 24, 18, 23, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(45L, LocalDateTime.of(2022, 8, 24, 18, 50, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(15L, LocalDateTime.of(2022, 8, 24, 18, 21, 50)));

		final List<Long> expectedIds = List.of(15L, 45L, 30L, 25L, 3L, 16L);

		final List<WorkOrderResponse> responseList = this.workOrderManagerService.getIds();
		final List<Long> actualIds = responseList.stream()
				.map(WorkOrderResponse::getId)
				.collect(Collectors.toList());

		assertEquals(actualIds, expectedIds);
	}

	@Test
	public void testDelete_Success() {
		final Long id = 1L;
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(id, LocalDateTime.now());
		this.workOrderManagerService.enqueue(workOrderRequest);
		final WorkOrderResponse response = this.workOrderManagerService.delete(id);
		assertEquals(response.getId(), id);
	}

	@Test
	public void testDelete_Failure() {
		assertThrows(WorkOrderNotFoundException.class, () -> this.workOrderManagerService.delete(1L));
	}

	@Test
	public void testGetPosition_Success() {
		final List<Long> expected = List.of(15L, 45L, 30L, 25L, 3L, 16L);

		this.workOrderManagerService.enqueue(new WorkOrderRequest(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(30L, LocalDateTime.of(2022, 8, 24, 19, 50, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(16L, LocalDateTime.of(2022, 8, 24, 18, 22, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(25L, LocalDateTime.of(2022, 8, 24, 18, 23, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(45L, LocalDateTime.of(2022, 8, 24, 18, 50, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(15L, LocalDateTime.of(2022, 8, 24, 18, 21, 50)));

		final WorkOrderResponse response = this.workOrderManagerService.getPosition(30L);

		assertEquals(response.getPosition(), expected.indexOf(30L));
	}

	@Test
	public void testGetPosition_Failure() {
		this.workOrderManagerService.enqueue(new WorkOrderRequest(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));

		assertThrows(WorkOrderNotFoundException.class, () -> this.workOrderManagerService.getPosition(30L));
	}

	@Test
	public void testComputeWaitingTime_Success() {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());
		final List<Long> idList = List.of(3L, 30L, 16L, 25L, 45L, 15L);

		this.workOrderManagerService.enqueue(new WorkOrderRequest(3L, LocalDateTime.of(2022, 8, 24, 18, 20, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(30L, LocalDateTime.of(2022, 8, 24, 19, 50, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(16L, LocalDateTime.of(2022, 8, 24, 18, 22, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(25L, LocalDateTime.of(2022, 8, 24, 18, 23, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(45L, LocalDateTime.of(2022, 8, 24, 18, 50, 50)));
		this.workOrderManagerService.enqueue(new WorkOrderRequest(15L, LocalDateTime.of(2022, 8, 24, 18, 21, 50)));

		final List<WorkOrderResponse> responseList = this.workOrderManagerService.computeWaitingTime(workOrderRequest);
		assertEquals(responseList.size(), idList.size());
		assertThat(responseList.stream()
				.map(WorkOrderResponse::getId)
				.collect(Collectors.toList())).containsAll(idList);
	}

	@Test
	public void testComputeWaitingTime_Failure() {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());
		assertThrows(WorkOrderException.class, () -> this.workOrderManagerService.computeWaitingTime(workOrderRequest));
	}

}
