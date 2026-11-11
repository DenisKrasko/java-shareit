package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private UserClient userClient;
	private UserController userController;

	@BeforeEach
	void setUp() {
		userClient = Mockito.mock(UserClient.class);
		userController = new UserController(userClient);
	}

	@Test
	void shouldCreateUser() {
		NewUserRequest request = new NewUserRequest("иван", "ivan@example.com");
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1));
		Mockito.when(userClient.createUser(request)).thenReturn(expected);

		assertThat(userController.addUser(request)).isEqualTo(expected);
		Mockito.verify(userClient).createUser(request);
	}

	@Test
	void shouldUpdateUser() {
		UpdateUserRequest request = new UpdateUserRequest(null, "пётр", "petr@example.com");
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("name", "пётр"));
		Mockito.when(userClient.updateUser(1L, request)).thenReturn(expected);

		assertThat(userController.update(1L, request)).isEqualTo(expected);
		Mockito.verify(userClient).updateUser(1L, request);
	}

	@Test
	void shouldGetUser() {
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1));
		Mockito.when(userClient.getUser(1L)).thenReturn(expected);

		assertThat(userController.getUser(1L)).isEqualTo(expected);
		Mockito.verify(userClient).getUser(1L);
	}

	@Test
	void shouldGetAllUsers() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1)));
		Mockito.when(userClient.getUsers()).thenReturn(expected);

		assertThat(userController.getAllUsers()).isEqualTo(expected);
		Mockito.verify(userClient).getUsers();
	}

	@Test
	void shouldDeleteUser() {
		ResponseEntity<Object> expected = ResponseEntity.ok().build();
		Mockito.when(userClient.deleteUser(1L)).thenReturn(expected);

		assertThat(userController.deleteUser(1L)).isEqualTo(expected);
		Mockito.verify(userClient).deleteUser(1L);
	}
}
