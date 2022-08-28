# work-order-management-system

A webservice application that implements the tasks of a work-order-management-system.

# Steps to run

1. Run `mvn clean install` to install all necessary dependencies
2. Locate WorkOrderManagerApplication.java under com.alvaria.workordermanager package.
3. Right click -> `Run as Java Application`

# Testing endpoints

1. Enqueue - Endpoint to add a work-order in the queue

HttpMethod: POST  
URL: http://localhost:8080/work-order-manager/enqueue

Sample Request: 
```
{
	"id": 55,
	"time": "2022-08-24T18:20:50"
}
```

Sample Response:   
Happy-path scenario:
```
{
    "id": 55,
    "time": "2022-08-24T18:20:50"
}
```
Failure scenario:
```
{
    "errorStatus": 400,
    "errorMessage": "Id already exists in the queue",
    "path": "uri=/work-order-manager/enqueue"
}
```

2. Dequeue - Endpoint to delete the top most work-order from the queue

HttpMethod: POST  
URL: http://localhost:8080/work-order-manager/dequeue

Sample Response:   
Happy-path scenario:
```
{
    "id": 55,
    "time": "2022-08-24T18:20:50"
}
```
Failure scenario:
```
{
    "errorStatus": 400,
    "errorMessage": "Queue is empty",
    "path": "uri=/work-order-manager/dequeue"
}
```

3. getIds - Endpoint to get all the ID's in the queue sorted from highest to lowest ranked orders.

HttpMethod: GET  
URL: http://localhost:8080/work-order-manager/getIds

Sample Response:  
```
[
    {
        "id": 55
    }
]
```

4. Delete - Endpoint to delete a specific ID from the queue.

HttpMethod: DELETE  
URL: http://localhost:8080/work-order-manager/55

Sample Response:   
Happy-path scenario:  
```
{
    "id": 55,
    "time": "2022-08-24T18:20:50"
}
```
Failure scenario:
```
{
    "errorStatus": 404,
    "errorMessage": "No such ID found in the queue",
    "path": "uri=/work-order-manager/55"
}
```

5. Position - Endpoint to get the position of a specific ID from the queue.

HttpMethod: GET  
URL: http://localhost:8080/work-order-manager/position/55

Sample Response:  
Happy-path scenario:
```
{
    "position": 0
}
```
Failure scenario:
```
{
    "errorStatus": 404,
    "errorMessage": "No such ID found in the queue",
    "path": "uri=/work-order-manager/position/55"
}
```

6. Compute-Waiting-Time - Endpoint to calculate the waiting time of each of the ID's in the queue

HttpMethod: POST  
URL: http://localhost:8080/work-order-manager/compute-waiting-time

Sample Request:  
```
{
	"time": "2022-08-28T19:01:00"
}
```
Sample Response:  
Happy-path scenario:
```
[
    {
        "id": 55,
        "waitingTime": 348010
    }
]
```
Failure scenario:
```
{
    "errorStatus": 400,
    "errorMessage": "Queue is empty",
    "path": "uri=/work-order-manager/compute-waiting-time"
}
```
