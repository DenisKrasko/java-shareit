package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
	private final List<User> users = new ArrayList<>();

	@Override
	public List<User> findAll() {
		return users;
	}

	@Override
	public User save(User user) {
		user.setId(getId());
		users.add(user);
		return user;
	}

	@Override
	public User update(User user) {
		return user;
	}

	@Override
	public void deleteUser(Long id) {
		users.removeIf(user -> user.getId().equals(id));
	}

	private long getId() {
		long lastId = users.stream()
				.mapToLong(User::getId)
				.max()
				.orElse(0);
		return lastId + 1;
	}

	public Optional<User> findById(Long userId) {
		return users.stream()
				.filter(user -> user.getId().equals(userId))
				.findFirst();
	}

	public boolean existsByEmail(String email) {
		return users.stream()
				.anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
	}

	public boolean existsById(Long userId) {
		return users.stream()
				.anyMatch(user -> user.getId().equals(userId));
	}
}