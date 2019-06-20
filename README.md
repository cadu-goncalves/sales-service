# Viniland Album Sales Service

## Description

Album Sales Service, with the following features:

- Query endpoints to recover album catalog;
- Query endpoints to recover sales;
- Sales registration, including cashback computation based on configurable rules.
- JWT support (in this implementation the issuer is the service itself)
- Data persistence to [MonogoDB](https://www.mongodb.com/)
- Open API Documentation


## Dependencies

#### Runtime

* [Java 8 or above](http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html)

#### Development

* [Java JDK 8 or above](http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)


## Structure

#### Main Dependencies

* [Spring Boot](https://spring.io/projects/spring-boot) Framework integration, dependency injection, MVC
* [Spring Data](https://spring.io/projects/spring-data) Data access model
* [Spring Security](https://spring.io/projects/spring-security) Authentication/authorization
* [Undertow](http://undertow.io/) Web server
* [Mongobee](https://github.com/mongobee/mongobee) Data migrations for MongoDB
* [Springfox](https://springfox.github.io/springfox/) Provides API automated documentation based on definitions pointed by [Swagger](https://swagger.io/)

 
## Build

The project ships with a Makefile that can be used as follows:

###### Start Test DB
Start MongoDB container
```
$ make start-testdb
```

###### Stop Test DB
Stop and destroys MongoDB container
```
$ make stop-testdb
```

###### Test
Perform all tests (auto start/stop test DB)
```
$ make test
```

###### Build
Build application fat JAR
```
$ make build
```

###### Run
Provides local application instance ready to use
```
$ make run-local
```