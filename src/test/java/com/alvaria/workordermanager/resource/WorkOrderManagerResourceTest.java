package com.alvaria.workordermanager.resource;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.WorkOrderRequest;
import com.alvaria.workordermanager.model.WorkOrderResponse;
import com.alvaria.workordermanager.service.WorkOrderManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(WorkOrderManagerResource.class)
public class WorkOrderManagerResourceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private WorkOrderManagerService workOrderManagerService;

	@Test
	public void testEnqueue_Success() throws Exception {
		final Long id = 1L;
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(id, LocalDateTime.now());
		final WorkOrderResponse workOrderResponse = WorkOrderResponse.builder()
				.id(id)
				.time(LocalDateTime.now())
				.build();
		when(this.workOrderManagerService.enqueue(Mockito.any(WorkOrderRequest.class))).thenReturn(workOrderResponse);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/work-order-manager/enqueue")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(workOrderRequest));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.id", is(id.intValue())));
	}

	@Test
	public void testEnqueue_Failure() throws Exception {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());
		doThrow(WorkOrderException.class).when(this.workOrderManagerService)
				.enqueue(Mockito.any(WorkOrderRequest.class));
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/work-order-manager/enqueue")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(workOrderRequest));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDequeue_Success() throws Exception {
		final Long id = 1L;
		final WorkOrderResponse workOrderResponse = WorkOrderResponse.builder()
				.id(id)
				.time(LocalDateTime.now())
				.build();
		when(this.workOrderManagerService.dequeue()).thenReturn(workOrderResponse);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/work-order-manager/dequeue")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.id", is(id.intValue())));
	}

	@Test
	public void testDequeue_Failure() throws Exception {
		doThrow(WorkOrderException.class).when(this.workOrderManagerService)
				.dequeue();
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/work-order-manager/dequeue")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder)
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetIds() throws Exception {
		final List<WorkOrderResponse> responseList = new ArrayList<>();
		responseList.add(WorkOrderResponse.builder()
				.id(1L)
				.build());
		responseList.add(WorkOrderResponse.builder()
				.id(2L)
				.build());
		responseList.add(WorkOrderResponse.builder()
				.id(3L)
				.build());

		when(this.workOrderManagerService.getIds()).thenReturn(responseList);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/work-order-manager/getIds")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()", is(responseList.size())));
	}

	@Test
	public void testDelete_Success() throws Exception {
		final Long id = 1L;
		final WorkOrderResponse response = WorkOrderResponse.builder()
				.id(id)
				.build();
		when(this.workOrderManagerService.delete(Mockito.anyLong())).thenReturn(response);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/work-order-manager/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(id));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.id", is(id.intValue())));
	}

	@Test
	public void testDelete_Failure() throws Exception {
		final Long id = 1L;
		doThrow(WorkOrderNotFoundException.class).when(this.workOrderManagerService)
				.delete(Mockito.anyLong());
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/work-order-manager/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(id));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetPosition_Success() throws Exception {
		final Long id = 1L;
		final int position = 3;
		final WorkOrderResponse response = WorkOrderResponse.builder()
				.position(position)
				.build();
		when(this.workOrderManagerService.getPosition(Mockito.anyLong())).thenReturn(response);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/work-order-manager/position/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(id));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.position", is(position)));
	}

	@Test
	public void testGetPosition_Failure() throws Exception {
		final Long id = 1L;
		doThrow(WorkOrderNotFoundException.class).when(this.workOrderManagerService)
				.getPosition(Mockito.anyLong());
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/work-order-manager/position/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(id));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isNotFound());
	}

	@Test
	public void testComputeWaitingTime() throws Exception {
		final WorkOrderRequest workOrderRequest = new WorkOrderRequest(1L, LocalDateTime.now());

		final List<WorkOrderResponse> responseList = new ArrayList<>();
		final WorkOrderResponse response1 = WorkOrderResponse.builder()
				.id(1L)
				.waitingTime(5L)
				.build();
		final WorkOrderResponse response2 = WorkOrderResponse.builder()
				.id(2L)
				.waitingTime(54L)
				.build();
		final WorkOrderResponse response3 = WorkOrderResponse.builder()
				.id(3L)
				.waitingTime(25L)
				.build();

		responseList.add(response1);
		responseList.add(response2);
		responseList.add(response3);

		when(this.workOrderManagerService.computeWaitingTime(Mockito.any(WorkOrderRequest.class)))
				.thenReturn(responseList);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/work-order-manager/compute-waiting-time")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(workOrderRequest));

		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.size()", is(responseList.size())));
	}

}
