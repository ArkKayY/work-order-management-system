package com.alvaria.workordermanager.resource;

import com.alvaria.workordermanager.model.Response;
import com.alvaria.workordermanager.model.WorkOrder;
import com.alvaria.workordermanager.service.WorkOrderManagerService;

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

import java.util.List;

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
    public ResponseEntity<Response> enqueue(@RequestBody final WorkOrder workOrder) {
        this.workOrderManagerService.enqueue(workOrder);
        // return the inserted workorder also along with the success msg
        return new ResponseEntity<>(new Response("Success"), HttpStatus.OK);
    }

    @PostMapping("/dequeue")
    public ResponseEntity<WorkOrder> dequeue() {
        final WorkOrder workOrder = this.workOrderManagerService.dequeue();
        return new ResponseEntity<>(workOrder, HttpStatus.OK);
    }

    @GetMapping("/getIds")
    public ResponseEntity<List<Response>> getIds() {
        final List<Response> responseList = this.workOrderManagerService.getIds();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable final Long id) {
        this.workOrderManagerService.delete(id);
        return new ResponseEntity<>(new Response("Successfully Deleted"), HttpStatus.OK);
    }

    @GetMapping("/position/{id}")
    public ResponseEntity<Response> getPosition(@PathVariable final Long id) {
        int position = this.workOrderManagerService.getPosition(id);
        return new ResponseEntity<>(new Response(position), HttpStatus.OK);
    }

    @PostMapping("/compute-waiting-time")
    public ResponseEntity<List<Response>> compute(@RequestBody final WorkOrder workOrder) {
        List<Response> responseList = this.workOrderManagerService.computeWaitingTime(workOrder);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

}
