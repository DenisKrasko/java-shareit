package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private UserService userService;
	private UserController userController;

	@BeforeEach
	void setUp() {
		userService = Mockito.mock(UserService.class);
		userController = new UserController(userService);
	}

	@Test
	void shouldCreateUser() {
		NewUserRequest request = new NewUserRequest("иван", "ivan@example.com");
		UserDto expected = new UserDto(1L, "иван", "ivan@example.com");
		Mockito.when(userService.addUser(request)).thenReturn(expected);

		assertThat(userController.addUser(request)).isEqualTo(expected);
		Mockito.verify(userService).addUser(request);
	}

	@Test
	void shouldUpdateUser() {
		UpdateUserRequest request = new UpdateUserRequest(null, "пётр", "petr@example.com");
		UserDto expected = new UserDto(1L, "пётр", "petr@example.com");
		Mockito.when(userService.update(1L, request)).thenReturn(expected);

		assertThat(userController.update(1L, request)).isEqualTo(expected);
		Mockito.verify(userService).update(1L, request);
	}

	@Test
	void shouldGetUser() {
		UserDto expected = new UserDto(1L, "иван", "ivan@example.com");
		Mockito.when(userService.getUser(1L)).thenReturn(expected);

		assertThat(userController.getUser(1L)).isEqualTo(expected);
		Mockito.verify(userService).getUser(1L);
	}

	@Test
	void shouldGetAllUsers() {
		List<UserDto> expected = List.of(new UserDto(1L, "иван", "ivan@example.com"));
		Mockito.when(userService.getAllUsers()).thenReturn(expected);

		assertThat(userController.getAllUsers()).isEqualTo(expected);
		Mockito.verify(userService).getAllUsers();
	}

	@Test
	void shouldDeleteUser() {
		userController.deleteUser(1L);

		Mockito.verify(userService).deleteUser(1L);
	}
}
