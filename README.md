# Getting Started

This a template service for Kotlin for Backend Developers mentoring program.

It's a Java based template and has to be migrated into kotlin and later updated according to the task.

The service uses [Dog CEO](https://dog.ceo/dog-api/) public API to obtain information about various dog breeds.

### The Task

1. **Migrate existing Java code into Kotlin** <br />
   You can use the build tool of your choice and the database of your choice, but your mentor should be able to run the service without additional preparations on his/her end.<br />
   On the start of the service the data is loaded from https://dog.ceo/api/breeds/list/all into the database. If the data already in db, this step is skipped.<br />
     `!` Use Spring WebClient to make a REST call to the api. <br />
     `!` Use Spring Data R2DBC to store the data into the database.<br />
     `!` Remove all unnecessary dependencies after the migration.<br />
     `!` You MUST use **coroutines** everywhere. **Flux and Mono are not allowed**!<br />
     `!` Try out CoroutineCrudRepository instead of CrudRepository

2. **Add controllers or router functions (if you want to go functional way)**
- Endpoint/router function that returns list of all breeds retrieved from db. <br />
  This endpoint is already defined in the template. You need to migrate it into kotlin. Make sure caching still works in a new version.<br />
  You can check cache hits in [Prometheus Metrics](http://localhost:8080/actuator/prometheus).
- Endpoint/router function that will retrieve all sub-breeds
- Endpoint/router function that will retrieve breeds that don't have sub-breeds
- Endpoint/router function that will retrieve sub-breeds of the specified breed
- Endpoint/router function that will retrieve image of the specified breed, store it in db and return back to the user. <br />
  To obtain the image, call https://dog.ceo/api/breed/{BREED_TYPE}/images. If there is already an image in db, just return it, if not call the mentioned api to obtain it.<br />

    `!` You MUST use **coroutines** everywhere. **Flux and Mono are not allowed**!<br />
    `!` Try out CoroutineCrudRepository instead of CrudRepository

3. **Add exception Handling**
- Handle cases like 400, 404, 500, failed calls to the api, etc. Preferably at a global level: https://www.baeldung.com/spring-webflux-errors#global

4. **Add tests**
- Add unit test for service layer using MockK framework and JUnit 5
- Add integration tests for controller layer

5. **Make sure your endpoints work via Swagger**
- Make sure swagger works with WebFlux. 

6. **Create a pull request and assign your mentor as a reviewer**


### Links for local development
* [Swagger](http://localhost:8080/swagger-ui/index.html)
* [Prometheus metrics](http://localhost:8080/actuator/prometheus)

### Reference Documentation

For further reference, please consider the following sections:

* [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
* [Spring WebClient with Kotlin extensions](https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines)
* [Spring Data R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/)
* [Spring and Kotlin](https://docs.spring.io/spring-framework/docs/current/reference/html/languages.html#languages)
* [Spring cache abstraction](https://docs.spring.io/spring-boot/docs/3.0.6/reference/htmlsingle/#io.caching)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.6/reference/htmlsingle/#data.sql.jpa-and-spring-data) 
* [Junit 5, Spring Boot Test](https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-testing.html)
* [Kotlin](https://spring.io/guides/tutorials/spring-boot-kotlin/) 
* [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) 
* [MockK](https://mockk.io/) 
* [Swagger](https://swagger.io/)

### Guides

The following guides illustrate how to use some features concretely:

* [Kotlin Coroutines](https://www.baeldung.com/kotlin/coroutines)
* [Spring Webflux and Kotlin](https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines)
* [Going Reactive with Spring, Coroutines and Kotlin Flow](https://spring.io/blog/2019/04/12/going-reactive-with-spring-coroutines-and-kotlin-flow)
* [Router Functions in Spring WebFlux](https://blog.knoldus.com/router-function-in-spring-webflux/)
* [Spring Functional Web](https://www.baeldung.com/spring-5-functional-web)
* [Spring Reactive Sample](https://hantsy.github.io/spring-reactive-sample/web/func.html)
* [MockK](https://www.baeldung.com/kotlin/mockk)



