package com.sothrose.todoapp.model;

import static com.google.common.collect.Lists.newArrayList;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "TODO_USER")
@Entity
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "todo_user_seq", allocationSize = 1)
  private Long id;

  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDate birthday;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id")
  private List<TodoItem> todoItems;

  public User(
      String username, String firstName, String lastName, String email, LocalDate birthday) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.birthday = birthday;
    todoItems = newArrayList();
  }

  public UserDto toDto() {
    return new UserDto(username, firstName, lastName, email, birthday, newArrayList(todoItems));
  }
}
