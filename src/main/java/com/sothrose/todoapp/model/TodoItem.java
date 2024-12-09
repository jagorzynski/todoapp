package com.sothrose.todoapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todo_item_seq")
  @SequenceGenerator(name = "todo_item_seq", sequenceName = "todo_item_seq", allocationSize = 1)
  private Long id;

  private String todo;
  private String info;
  private LocalDateTime completionTime;
  private boolean isDone;

  public TodoItem(String todo, String info, LocalDateTime completionTime, boolean isDone) {
    this.todo = todo;
    this.info = info;
    this.completionTime = completionTime;
    this.isDone = isDone;
  }

  public TodoItemDto toDto() {
    return new TodoItemDto(todo, info, completionTime, isDone);
  }
}
