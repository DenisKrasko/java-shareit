package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Service
public class UserClient extends BaseClient {
	private static final String USERS_PATH = "/users";

	private final String usersUrl;

	public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(builder.build());
		this.usersUrl = serverUrl + USERS_PATH;
	}

	public ResponseEntity<Object> createUser(NewUserRequest request) {
		return post(usersUrl, request);
	}

	public ResponseEntity<Object> updateUser(long userId, UpdateUserRequest request) {
		return patch(usersUrl + "/" + userId, request);
	}

	public ResponseEntity<Object> getUser(long userId) {
		return get(usersUrl + "/" + userId);
	}

	public ResponseEntity<Object> getUsers() {
		return get(usersUrl);
	}

	public ResponseEntity<Object> deleteUser(long userId) {
		return delete(usersUrl + "/" + userId);
	}
}
