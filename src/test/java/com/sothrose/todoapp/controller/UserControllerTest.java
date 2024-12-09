package com.sothrose.todoapp.controller;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sothrose.todoapp.model.TodoItemDto;
import com.sothrose.todoapp.model.UserDto;
import com.sothrose.todoapp.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  private AutoCloseable autoCloseable;

  @BeforeEach
  void setUp() {
    autoCloseable = openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  void shouldSaveUser() throws Exception {
    // given
    var userDto =
        new UserDto("johndo", "john", "do", "john.doe@example.com", LocalDate.of(1999, 11, 1));
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    var requestBody = objectMapper.writeValueAsString(userDto);

    // when
    mockMvc
        .perform(post("/v1/todoapp/users").contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());

    // then
    verify(userService).saveUser(userDto);
  }

  @Test
  void shouldGetUserById() throws Exception {
    // given
    var userId = 1L;
    var userDto =
        new UserDto("johndo", "john", "do", "john.doe@example.com", LocalDate.of(1999, 11, 1));
    when(userService.getUser(userId)).thenReturn(userDto);

    // when
    mockMvc
        .perform(get("/v1/todoapp/users/{userId}", userId).accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("johndo"))
        .andExpect(jsonPath("$.firstName").value("john"))
        .andExpect(jsonPath("$.lastName").value("do"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.birthday").value("1999-11-01"));

    // then
    verify(userService).getUser(userId);
  }

  @Test
  void shouldGetAllUsers() throws Exception {
    // given
    var users =
        List.of(
            new UserDto("johndo", "john", "do", "john.doe@example.com", LocalDate.of(1999, 11, 1)),
            new UserDto("janedo", "jane", "do", "jane.doe@example.com", LocalDate.of(1999, 2, 2)));
    when(userService.getAllUsers()).thenReturn(users);

    // when
    mockMvc
        .perform(get("/v1/todoapp/users").accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

    // then
    verify(userService).getAllUsers();
  }

  @Test
  void shouldDeleteUser() throws Exception {
    // given
    var userId = 1L;

    // when
    mockMvc.perform(delete("/v1/todoapp/users/{userId}", userId)).andExpect(status().isOk());

    // then
    verify(userService).deleteUser(userId);
  }

  @Test
  void shouldAddTodoItemToUser() throws Exception {
    // given
    var todoItemDto =
        new TodoItemDto(
            1L,
            "Do something",
            "Sample Todo Item",
            LocalDateTime.of(2024, 12, 25, 11, 11, 11),
            false);
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    var requestBody = objectMapper.writeValueAsString(todoItemDto);

    // when
    mockMvc
        .perform(
            post("/v1/todoapp/users/todoItems").contentType(APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());

    // then
    verify(userService).addTodoItemToUser(todoItemDto);
  }

  @Test
  void shouldDeleteTodoItemFromUser() throws Exception {
    // given
    Long userId = 1L;
    Long todoItemId = 1L;

    // when
    mockMvc
        .perform(
            delete("/v1/todoapp/users/todoItems")
                .param("userId", userId.toString())
                .param("todoItemId", todoItemId.toString()))
        .andExpect(status().isOk());

    // then
    verify(userService).deleteTodoItemFromUser(userId, todoItemId);
  }

  @Test
  void shouldGetAllDoneTodoItems() throws Exception {
    // given
    Long userId = 1L;
    var doneTodoItems =
        List.of(
            new TodoItemDto(
                1L,
                "Do something",
                "Sample Todo Item",
                LocalDateTime.of(2024, 12, 25, 11, 11, 11),
                false));
    when(userService.getAllDoneTodoItemsForUser(userId)).thenReturn(doneTodoItems);

    // when
    mockMvc
        .perform(get("/v1/todoapp/users/todoItems/done/{userId}", userId).accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(1L))
        .andExpect(jsonPath("$[0].todo").value("Do something"))
        .andExpect(jsonPath("$[0].info").value("Sample Todo Item"))
        .andExpect(jsonPath("$[0].completionTime").value("2024-12-25T11:11:11"));

    // then
    verify(userService).getAllDoneTodoItemsForUser(userId);
  }

  @Test
  void shouldGetAllNotDoneTodoItems() throws Exception {
    // given
    Long userId = 1L;
    var notDoneTodoItems =
        List.of(
            new TodoItemDto(
                1L,
                "Do something",
                "Sample Todo Item",
                LocalDateTime.of(2024, 12, 25, 11, 11, 11),
                false));
    when(userService.getAllNotDoneTodoItemsForUser(userId)).thenReturn(notDoneTodoItems);

    // when
    mockMvc
        .perform(
            get("/v1/todoapp/users/todoItems/notDone/{userId}", userId).accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(1L))
        .andExpect(jsonPath("$[0].todo").value("Do something"))
        .andExpect(jsonPath("$[0].info").value("Sample Todo Item"))
        .andExpect(jsonPath("$[0].completionTime").value("2024-12-25T11:11:11"));

    // then
    verify(userService).getAllNotDoneTodoItemsForUser(userId);
  }
}
