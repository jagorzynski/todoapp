package com.sothrose.todoapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
  @NotNull @NotEmpty private String username;
  @NotNull @NotEmpty private String firstName;
  @NotNull @NotEmpty private String lastName;
  @NotNull @NotEmpty @Email private String email;
  @NotNull private LocalDate birthday;
  private List<TodoItem> todoItems;

  public UserDto(
      String username, String firstName, String lastName, String email, LocalDate birthday) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.birthday = birthday;
  }

  public User toUser() {
    return new User(username, firstName, lastName, email, birthday);
  }
}
