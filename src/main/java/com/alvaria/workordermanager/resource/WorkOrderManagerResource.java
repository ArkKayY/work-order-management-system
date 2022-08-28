package com.alvaria.workordermanager.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvaria.workordermanager.model.WorkOrderRequest;
import com.alvaria.workordermanager.model.WorkOrderResponse;
import com.alvaria.workordermanager.service.WorkOrderManagerService;

@RestController
@RequestMapping(path = "/work-order-manager",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkOrderManagerResource {

	private final WorkOrderManagerService workOrderManagerService;

	@Autowired
	public WorkOrderManagerResource(final WorkOrderManagerService workOrderManagerService) {
		this.workOrderManagerService = workOrderManagerService;
	}

	@PostMapping("/enqueue")
	public ResponseEntity<WorkOrderResponse> enqueue(@RequestBody final WorkOrderRequest workOrderRequest) {
		final WorkOrderResponse workOrderResponse = this.workOrderManagerService.enqueue(workOrderRequest);
		return new ResponseEntity<>(workOrderResponse, HttpStatus.OK);
	}

	@PostMapping("/dequeue")
	public ResponseEntity<WorkOrderResponse> dequeue() {
		final WorkOrderResponse workOrderResponse = this.workOrderManagerService.dequeue();
		return new ResponseEntity<>(workOrderResponse, HttpStatus.OK);
	}

	@GetMapping("/getIds")
	public ResponseEntity<List<WorkOrderResponse>> getIds() {
		final List<WorkOrderResponse> responseList = this.workOrderManagerService.getIds();
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<WorkOrderResponse> delete(@PathVariable final Long id) {
		final WorkOrderResponse workOrderResponse = this.workOrderManagerService.delete(id);
		return new ResponseEntity<>(workOrderResponse, HttpStatus.OK);
	}

	@GetMapping("/position/{id}")
	public ResponseEntity<WorkOrderResponse> getPosition(@PathVariable final Long id) {
		final WorkOrderResponse workOrderResponse = this.workOrderManagerService.getPosition(id);
		return new ResponseEntity<>(workOrderResponse, HttpStatus.OK);
	}

	@PostMapping("/compute-waiting-time")
	public ResponseEntity<List<WorkOrderResponse>> compute(@RequestBody final WorkOrderRequest workOrderRequest) {
		final List<WorkOrderResponse> responseList = this.workOrderManagerService.computeWaitingTime(workOrderRequest);
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

}
