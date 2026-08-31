package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
	private final UserService userService;

	@PostMapping
	public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
		return userService.addUser(newUserRequest);
	}

	@PutMapping
	public UserDto update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
		return userService.update(updateUserRequest);
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		return userService.getAllUsers();
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
}
