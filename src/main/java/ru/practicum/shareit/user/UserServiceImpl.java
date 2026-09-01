package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserMapper::toUserDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto getUser(Long id) {
		return UserMapper.toUserDto(userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User not found with id: " + id)));
	}


	@Override
	public UserDto addUser(NewUserRequest newUserRequest) {
		validateEmailUniqueness(newUserRequest.getEmail());
		User user = UserMapper.toUser(newUserRequest);
		User savedUser = userRepository.save(user);
		return UserMapper.toUserDto(savedUser);
	}

	@Override
	public UserDto update(Long id, UpdateUserRequest updateUserRequest) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
		validateEmailUniqueness(updateUserRequest.getEmail());
		UserMapper.updateUserFields(user, updateUserRequest);
		User updatedUser = userRepository.update(user);
		return UserMapper.toUserDto(updatedUser);
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteUser(id);
	}

	private void validateEmailUniqueness(String email) {
		if (userRepository.existsByEmail(email)) {
			log.error("Пользователь с таким email уже существует");
			throw new ValidationException("Пользователь с таким email уже существует");
		}
	}
}
