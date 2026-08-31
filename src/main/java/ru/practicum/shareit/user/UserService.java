package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
	List<UserDto> getAllUsers();
	UserDto addUser(NewUserRequest newUserRequest);
	UserDto update(UpdateUserRequest updateUserRequest);
	void deleteUser(Long id);
}
