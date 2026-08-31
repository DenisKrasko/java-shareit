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
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream()
				.map(UserMapper::toUserDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto addUser(NewUserRequest newUserRequest) {
		validateEmailUniqueness(newUserRequest.getEmail());
		User user = UserMapper.toUser(newUserRequest);
		User savedUser = userRepository.save(user);
		return UserMapper.toUserDto(savedUser);
	}

	@Override
	public UserDto update(UpdateUserRequest updateUserRequest) {
		if (updateUserRequest.getId() == null) {
			log.error("Error: uninitialised id");
			throw new ValidationException("Id должен быть указан");
		}
		validateEmailUniqueness(updateUserRequest.getEmail());
		User updatedUser = userRepository.findById(updateUserRequest.getId())
				.map(user -> UserMapper.updateUserFields(user, updateUserRequest))
				.orElseThrow(() -> new NotFoundException("Пользователь не найден"));
		return UserMapper.toUserDto(userRepository.update(updatedUser));
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
