# Booking System

Booking system which include postgres, kafka and redis.


### Prerequisites
Application can be run using docker-compose or running BookingSystemApplication.java but firstly need to make minor changes in application.yml and docker compose files - need to uncomment 'local run lines' and comment 'docker run'
Rebuild app:
./gradlew clean build

Commands to manage app:
docker-compose up
Stop application.
docker-compose down

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

