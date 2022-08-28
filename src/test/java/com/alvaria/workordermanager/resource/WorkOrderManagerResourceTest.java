package com.alvaria.workordermanager.resource;

import com.alvaria.workordermanager.exception.WorkOrderException;
import com.alvaria.workordermanager.exception.WorkOrderNotFoundException;
import com.alvaria.workordermanager.model.Response;
import com.alvaria.workordermanager.model.WorkOrder;
import com.alvaria.workordermanager.service.WorkOrderManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());
        doNothing().when(this.workOrderManagerService).enqueue(Mockito.any(WorkOrder.class));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/work-order-manager/enqueue")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(workOrder));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.message", is("Success")));
    }

    @Test
    public void testEnqueue_Failure() throws Exception {
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());
        doThrow(WorkOrderException.class).when(this.workOrderManagerService).enqueue(Mockito.any(WorkOrder.class));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/work-order-manager/enqueue")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(workOrder));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDequeue_Success() throws Exception {
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());
        when(this.workOrderManagerService.dequeue()).thenReturn(workOrder);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/work-order-manager/dequeue");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void testDequeue_Failure() throws Exception {
        doThrow(WorkOrderException.class).when(this.workOrderManagerService).dequeue();
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/work-order-manager/dequeue");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetIds() throws Exception {
        final List<Long> idList = List.of(1L, 2L, 3L);
        //        when(this.workOrderManagerService.getIds()).thenReturn(idList);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/work-order-manager/getIds");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(idList.size())));
    }

    @Test
    public void testDelete_Success() throws Exception {
        Long id = 1L;
        doNothing().when(this.workOrderManagerService).delete(Mockito.anyLong());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/work-order-manager/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(id));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.message", is("Successfully Deleted")));
    }

    @Test
    public void testDelete_Failure() throws Exception {
        Long id = 1L;
        doThrow(WorkOrderNotFoundException.class).when(this.workOrderManagerService).delete(Mockito.anyLong());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/work-order-manager/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(id));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPosition_Success() throws Exception {
        Long id = 1L;
        int position = 3;
        when(this.workOrderManagerService.getPosition(Mockito.anyLong())).thenReturn(position);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/work-order-manager/position/{id}", id)
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
        Long id = 1L;
        doThrow(WorkOrderNotFoundException.class).when(this.workOrderManagerService).getPosition(Mockito.anyLong());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/work-order-manager/position/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(id));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testComputeWaitingTime() throws Exception {
        final WorkOrder workOrder = new WorkOrder(1L, LocalDateTime.now());

        final List<Response> responseList = new ArrayList<>();
        responseList.add(new Response(1L, 5.0));
        responseList.add(new Response(2L, 54.0));
        responseList.add(new Response(3L, 25.0));

        when(this.workOrderManagerService.computeWaitingTime(Mockito.any(WorkOrder.class))).thenReturn(responseList);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/work-order-manager/compute-waiting-time")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(workOrder));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.size()", is(responseList.size())));
    }

}
