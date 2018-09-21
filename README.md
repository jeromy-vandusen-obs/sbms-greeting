# Spring Boot Microservices - Greeting Service

## Jenkins Job Configuration

1. On the main Jenkins dashboard page, click "New Item".
2. Enter "sbms-greeting" in the "name" field, select "Pipeline", and click "OK".
3. Scroll down to the "Pipeline" section and in "Definition" select "Pipeline script from SCM".
4. In "SCM", select "Git".
5. In "Repository URL", enter "https://github.com/jeromy-vandusen-obs/sbms-greeting.git".
6. Deselect "Lightweight checkout" and click "Save".

## Profiles

To run locally for development purposes, use the `dev` profile.

To run in a Docker swarm, use the `swarm` profile.

## Environment variables

* EUREKA_INSTANCE_LIST: A comma-separated list of Eureka discovery service URLs. The default value is
`http://localhost:8761/eureka/`.

## Endpoints

* `/identity`: provides information about the service, including the service name as it appears in Eureka, the
version number, and the API level, which is an integer value representing the highest version number that appears
in an endpoint path. For example, if the only endpoint defined in the service has as its path `/v1/getStuff`, then
the API level is 1. If there are two endpoints with paths `/v1/getStuff` and `/v2/getNewerStuff`, then the API
level is 2.
* `/api`: provides a list of all endpoints defined for the server. This list _does not_ include Actuator endpoints.

## To Do

* Add security.
* Add a dependent service and implement the `/dependencyHealth` endpoint.
