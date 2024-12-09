package com.sothrose.todoapp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.sothrose.todoapp.model.TodoItemDto;
import com.sothrose.todoapp.model.UserDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerIT {

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:15.3")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("testpass")
          .withInitScript("schema.sql");

  @DynamicPropertySource
  static void setDatasourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
    registry.add("spring.flyway.baselineOnMigrate", () -> "true");
  }

  @LocalServerPort private int port;

  private WebClient webClient;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    webClient = WebClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void shouldSaveUser() {
    var userDto =
        new UserDto("johndo", "John", "Doe", "john.doe@example.com", LocalDate.of(1999, 11, 1));

    var response =
        webClient
            .post()
            .uri("/v1/todoapp/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(userDto), UserDto.class)
            .retrieve()
            .toBodilessEntity()
            .block();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    int userCount =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM todo_user WHERE username = ?", Integer.class, "johndo");
    assertThat(userCount).isEqualTo(1);
  }

  @Test
  void shouldGetUserById() {
    Long userId = 1L;

    var user =
        webClient
            .get()
            .uri("/v1/todoapp/users/{userId}", userId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(UserDto.class)
            .block();

    assertThat(user).isNotNull();
    assertThat(user.getUsername()).isEqualTo("johndoe");
  }

  @Test
  void shouldGetAllUsers() {
    var users =
        webClient
            .get()
            .uri("/v1/todoapp/users")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(UserDto.class)
            .collectList()
            .block();

    assertThat(users).isNotNull();
    assertThat(users).hasSize(5);
    assertThat(users.get(0).getEmail()).isEqualTo("john.doe@example.com");
  }

  @Test
  void shouldDeleteUser() {
    Long userId = 1L;

    var response =
        webClient
            .delete()
            .uri("/v1/todoapp/users/{userId}", userId)
            .retrieve()
            .toBodilessEntity()
            .block();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    int userCount =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM todo_user WHERE id = ?", Integer.class, userId);
    assertThat(userCount).isEqualTo(0);
  }

  @Test
  void shouldAddTodoItemToUser() {
    var todoItem =
        new TodoItemDto(1L, "Do something", "Sample Todo", LocalDateTime.now().plusDays(1), false);

    var response =
        webClient
            .post()
            .uri("/v1/todoapp/users/todoItems")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(todoItem), TodoItemDto.class)
            .retrieve()
            .toBodilessEntity()
            .block();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    int todoCount =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM todo_item WHERE info = ?", Integer.class, "Sample Todo");
    assertThat(todoCount).isEqualTo(1);
  }

  @Test
  void shouldDeleteTodoItemFromUser() {
    Long userId = 1L;
    Long todoItemId = 1L;

    var response =
        webClient
            .delete()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/v1/todoapp/users/todoItems")
                        .queryParam("userId", userId)
                        .queryParam("todoItemId", todoItemId)
                        .build())
            .retrieve()
            .toBodilessEntity()
            .block();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    int todoCount =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM todo_item WHERE id = ?", Integer.class, todoItemId);
    assertThat(todoCount).isEqualTo(0);
  }

  @Test
  void shouldGetAllDoneTodoItems() {
    Long userId = 1L;

    var doneItems =
        webClient
            .get()
            .uri("/v1/todoapp/users/todoItems/done/{userId}", userId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(TodoItemDto.class)
            .collectList()
            .block();

    assertThat(doneItems).isNotNull();
    assertThat(doneItems).allMatch(TodoItemDto::isDone);
  }

  @Test
  void shouldGetAllNotDoneTodoItems() {
    Long userId = 1L;

    var notDoneItems =
        webClient
            .get()
            .uri("/v1/todoapp/users/todoItems/notDone/{userId}", userId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(TodoItemDto.class)
            .collectList()
            .block();

    assertThat(notDoneItems).isNotNull();
    assertThat(notDoneItems).allMatch(todoItem -> !todoItem.isDone());
  }
}
