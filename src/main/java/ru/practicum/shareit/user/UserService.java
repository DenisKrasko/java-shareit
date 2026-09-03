package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
	List<UserDto> getAllUsers();

	UserDto getUser(Long id);

	UserDto addUser(NewUserRequest newUserRequest);

	UserDto update(Long id, UpdateUserRequest updateUserRequest);

	void deleteUser(Long id);
}
