package com.sothrose.todoapp.service;

import static java.lang.String.format;

import com.sothrose.todoapp.exception.UserNotFoundException;
import com.sothrose.todoapp.model.TodoItem;
import com.sothrose.todoapp.model.TodoItemDto;
import com.sothrose.todoapp.model.User;
import com.sothrose.todoapp.model.UserDto;
import com.sothrose.todoapp.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  public void saveUser(UserDto userDto) {
    log.info("Saving new user with username: [{}]", userDto.getUsername());
    var savedUser = userRepository.save(userDto.toUser());
    log.info(
        "User with username: [{}] saved successfully user id: [{}]",
        userDto.getUsername(),
        savedUser.getId());
  }

  public UserDto getUser(Long userId) {
    log.info("Getting user with id: [{}]", userId);
    return userRepository
        .findById(userId)
        .map(User::toDto)
        .orElseThrow(
            () -> new UserNotFoundException(format("User with id: [%s] not found", userId)));
  }

  public void deleteUser(Long userId) {
    log.info("Deleting user with id: [{}]", userId);
    userRepository.deleteById(userId);
  }

  public List<UserDto> getAllUsers() {
    log.info("Getting all users");
    return userRepository.findAll().stream().map(User::toDto).toList();
  }

  public void addTodoItemToUser(TodoItemDto todoItemDto) {
    log.info("Adding new todoItem to user with id: [{}]", todoItemDto.getUserId());
    var userOpt = userRepository.findById(todoItemDto.getUserId());
    if (userOpt.isEmpty()) {
      throw new UserNotFoundException(
          format("User with id: [%s] not found", todoItemDto.getUserId()));
    }

    var user = userOpt.get();
    user.getTodoItems().add(todoItemDto.toTodoItem());
    userRepository.save(user);
    log.info("TodoItem successfully added to user with id: [{}]", todoItemDto.getUserId());
  }

  public void deleteTodoItemFromUser(Long userId, Long todoItemId) {
    log.info("Deleting todoItem with id: [{}] from user with id: [{}] started", todoItemId, userId);
    var userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      throw new UserNotFoundException(format("User with id: [%s] not found", userId));
    }

    var user = userOpt.get();
    user.getTodoItems().removeIf(todoItem -> todoItem.getId().equals(todoItemId));
    userRepository.save(user);
    log.info(
        "Deleting todoItem with id: [{}] from user with id: [{}] succeeded", todoItemId, userId);
  }

  public List<TodoItemDto> getAllDoneTodoItemsForUser(Long userId) {
    log.info("Getting all done todoItems for a user with id: [{}]", userId);
    var userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      throw new UserNotFoundException(format("User with id: [%s] not found", userId));
    }

    return userOpt.get().getTodoItems().stream()
        .filter(TodoItem::isDone)
        .map(TodoItem::toDto)
        .toList();
  }

  public List<TodoItemDto> getAllNotDoneTodoItemsForUser(Long userId) {
    log.info("Getting all not done todoItems for a user with id: [{}]", userId);
    var userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      throw new UserNotFoundException(format("User with id: [%s] not found", userId));
    }

    return userOpt.get().getTodoItems().stream()
        .filter(todoItem -> !todoItem.isDone())
        .map(TodoItem::toDto)
        .toList();
  }
}
