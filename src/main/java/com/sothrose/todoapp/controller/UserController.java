package com.sothrose.todoapp.controller;

import com.sothrose.todoapp.model.TodoItemDto;
import com.sothrose.todoapp.model.UserDto;
import com.sothrose.todoapp.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/v1/todoapp/users")
public class UserController {

  private final UserService userService;

  @PostMapping(consumes = "application/json")
  public void saveUser(@Valid @RequestBody UserDto userDto) {
    userService.saveUser(userDto);
  }

  @GetMapping(path = "/{userId}", produces = "application/json")
  public UserDto getUser(@PathVariable Long userId) {
    return userService.getUser(userId);
  }

  @GetMapping(produces = "application/json")
  public List<UserDto> getAll() {
    return userService.getAllUsers();
  }

  @DeleteMapping(path = "/{userId}")
  public void deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
  }

  @PostMapping(path = "/todoItems", consumes = "application/json")
  public void addTodoItemToUser(@Valid @RequestBody TodoItemDto todoItemDto) {
    userService.addTodoItemToUser(todoItemDto);
  }

  @DeleteMapping(path = "/todoItems")
  public void deleteTodoItemFromUser(@RequestParam Long userId, @RequestParam Long todoItemId) {
    userService.deleteTodoItemFromUser(userId, todoItemId);
  }

  @GetMapping(path = "/todoItems/done/{userId}", produces = "application/json")
  public List<TodoItemDto> getAllDone(@PathVariable Long userId) {
    return userService.getAllDoneTodoItemsForUser(userId);
  }

  @GetMapping(path = "/todoItems/notDone/{userId}", produces = "application/json")
  public List<TodoItemDto> getAllNotDone(@PathVariable Long userId) {
    return userService.getAllNotDoneTodoItemsForUser(userId);
  }
}
