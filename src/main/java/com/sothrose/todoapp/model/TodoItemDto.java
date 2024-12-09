package com.sothrose.todoapp.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import lombok.*;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoItemDto {
  @NotNull private Long userId;
  @NotNull @NotEmpty private String todo;
  @NotNull @NotEmpty private String info;
  @NotNull @FutureOrPresent private LocalDateTime completionTime;
  private boolean isDone;

  public TodoItemDto(String todo, String info, LocalDateTime completionTime, boolean isDone) {
    this.todo = todo;
    this.info = info;
    this.completionTime = completionTime;
    this.isDone = isDone;
  }

  public TodoItem toTodoItem() {
    return new TodoItem(todo, info, completionTime, isDone);
  }
}
