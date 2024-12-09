package com.sothrose.todoapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sothrose.todoapp.model.User;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class UserRepositoryTest {
  @Autowired private TestEntityManager entityManager;

  @Autowired private UserRepository userRepository;

  @Test
  public void shouldFindUserById() {
    // given
    var user = new User("johndo", "john", "do", "john@do.pl", LocalDate.of(1985, 4, 12));
    entityManager.persistAndFlush(user);

    // when
    var foundUser = userRepository.findById(user.getId()).orElse(null);

    // then
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
  }

  @Test
  public void shouldFindAllUsers() {
    // given
    var johnDo = new User("johndo", "john", "do", "john@do.pl", LocalDate.of(1985, 4, 12));
    var janeDo = new User("janedo", "jane", "do", "jane@do.pl", LocalDate.of(1985, 4, 12));
    entityManager.persist(johnDo);
    entityManager.persist(janeDo);
    entityManager.flush();

    // when
    var allUsers = userRepository.findAll();

    // then
    assertThat(allUsers).isNotEmpty();
    assertThat(allUsers).hasSize(2);
    assertThat(allUsers.get(0).getUsername()).isEqualTo("johndo");
    assertThat(allUsers.get(1).getUsername()).isEqualTo("janedo");
  }

  @Test
  public void shouldSaveUser() {
    // given
    var newUser = new User("johndo", "john", "do", "john@do.pl", LocalDate.of(1985, 4, 12));

    // when
    var savedUser = userRepository.save(newUser);

    // then
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getUsername()).isEqualTo(newUser.getUsername());
  }

  @Test
  public void shouldDeleteUser() {
    // given
    var user = new User("johndo", "john", "do", "john@do.pl", LocalDate.of(1985, 4, 12));
    entityManager.persistAndFlush(user);
    var userId = user.getId();

    // when
    userRepository.deleteById(userId);

    // then
    var deletedUser = userRepository.findById(userId).orElse(null);
    assertThat(deletedUser).isNull();
  }
}
