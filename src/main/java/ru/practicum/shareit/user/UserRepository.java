package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	List<User> findAll();

	User save(User user);

	User update(User user);

	void deleteUser(Long id);

	Optional<User> findById(Long userId);

	boolean existsByEmail(String email);
}
