package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
	private final UserClient userClient;

	@PostMapping
	public ResponseEntity<Object> addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
		return userClient.createUser(newUserRequest);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Object> update(@PathVariable Long id,
										 @Valid @RequestBody UpdateUserRequest updateUserRequest) {
		return userClient.updateUser(id, updateUserRequest);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) {
		return userClient.getUser(id);
	}

	@GetMapping
	public ResponseEntity<Object> getAllUsers() {
		return userClient.getUsers();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		return userClient.deleteUser(id);
	}
}
