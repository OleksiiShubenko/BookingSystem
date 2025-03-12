# Booking System

Booking system which include postgres, kafka and redis.


### Prerequisites

Postgres, kafka and redis are run in docker. There are approaches to run application:
1. Application can be run locally using IDEA configuration running main from BookingSystemApplication.java:
* Run command: `docker compose -f docker-compose-local.yaml up` to up services;
* Then run main method from BookingSystemApplication.java.

2. Application can be run using docker compose:
* Run command: `docker compose -f docker-compose.yaml up` to up all services including booking-system;

Before running clean build directory and build command: `./gradlew clean build`

Docker postgres volume also can be removed if any inconsistent data appears.  
## Commands
Swagger page:
http://localhost:8080/swagger-ui/index.html


To create a new user:
POST http://localhost:8080/admin/user
Body: {"username": "user1", "password": "pass1" }


To create a new unit:
POST http://localhost:8080/unit
Body:
{
"username": "user1"
"numRooms": 3,
"type": "HOME",
"floor": 3,
"cost": 2500,
"description": "HOME"
}


To book an existing unit:
POST: http://localhost:8080/booking
Body:
{
"username": "user1",
"unitId": 1,
"fromTime": "2024-03-06T11:10:45Z",
"toTime": "2024-03-06T11:20:45Z"
}


To process payment:
POST: http://localhost:8080/payment/process
Body:
{
"transactionId": "d1b3b2dd-9f22-4035-91f9-836a118c1620 is created.",
"funds": 40
}
transactionId - will be returned in booking details from booing unit endpoint


To cancel a payment:
PUT: http://localhost:8080/booking/cancel
Body:
{
"username": "user1",
"unitId": 6,
"fromTime": "2024-03-06T11:10:45Z",
"toTime": "2024-03-06T11:20:45Z"
}


Extended search:
GET: http://localhost:8080/unit/search?sortBy=cost&fromTime=2024-03-06T10:10:45Z&toTime=2024-03-06T14:00:45Z&minCost=400&maxCost=500&sortOrder=DESC
Or body example:
{
"minCost": 500,
"maxCost": 1000,
"sortBy": "COST",
"sortOrder": "DESC"
}

An availability check endpoint:
GET: http://localhost:8080/unit/availability

