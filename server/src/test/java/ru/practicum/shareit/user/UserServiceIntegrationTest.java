package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void shouldCreateUser() {
		UserDto user = userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));

		assertThat(user.getId()).isNotNull();
		assertThat(user.getName()).isEqualTo("Иван");
		assertThat(user.getEmail()).isEqualTo("ivan@example.com");
		assertThat(userRepository.findById(user.getId())).isPresent();
	}

	@Test
	void shouldGetAllUsers() {
		userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));
		userService.addUser(new NewUserRequest("Пётр", "petr@example.com"));

		List<UserDto> users = userService.getAllUsers();

		assertThat(users).hasSize(2);
	}

	@Test
	void shouldGetUserById() {
		UserDto created = userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));

		UserDto found = userService.getUser(created.getId());

		assertThat(found).isEqualTo(created);
	}

	@Test
	void shouldUpdateUserName() {
		UserDto created = userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));

		UserDto updated = userService.update(created.getId(), new UpdateUserRequest(null, "Пётр", null));

		assertThat(updated.getName()).isEqualTo("Пётр");
		assertThat(updated.getEmail()).isEqualTo("ivan@example.com");
	}

	@Test
	void shouldUpdateUserEmail() {
		UserDto created = userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));

		UserDto updated = userService.update(created.getId(),
				new UpdateUserRequest(null, null, "new@example.com"));

		assertThat(updated.getName()).isEqualTo("Иван");
		assertThat(updated.getEmail()).isEqualTo("new@example.com");
	}

	@Test
	void shouldDeleteUser() {
		UserDto created = userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));

		userService.deleteUser(created.getId());

		assertThat(userRepository.findById(created.getId())).isEmpty();
	}

	@Test
	void shouldThrowWhenEmailAlreadyExists() {
		userService.addUser(new NewUserRequest("Иван", "ivan@example.com"));

		assertThatThrownBy(() -> userService.addUser(new NewUserRequest("Пётр", "ivan@example.com")))
				.isInstanceOf(DuplicateEmailException.class);
	}

	@Test
	void shouldThrowWhenUserNotFound() {
		assertThatThrownBy(() -> userService.getUser(999L))
				.isInstanceOf(NotFoundException.class);
	}
}
