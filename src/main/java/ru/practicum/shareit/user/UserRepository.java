package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findAll();

	User save(User user);

	User update(User user);

	void deleteUser(Long id);

	Optional<User> findById(Long userId);

	boolean existsByEmail(String email);

	boolean existsById(Long userId);
}
