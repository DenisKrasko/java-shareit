package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
	public static UserDto toUserDto(User user) {
		return new UserDto(
				user.getName(),
				user.getEmail()
		);
	}

	public static User toUser(NewUserRequest newUserRequest) {
		User user = new User();
		user.setEmail(newUserRequest.getEmail());
		user.setName(newUserRequest.getName());
		return user;
	}

	public static User updateUserFields(User user, UpdateUserRequest updateUserRequest) {
		if (updateUserRequest.hasEmail()) {
			user.setEmail(updateUserRequest.getEmail());
		}
		if (updateUserRequest.hasName()) {
			user.setName(updateUserRequest.getName());
		}
		return user;
	}
}