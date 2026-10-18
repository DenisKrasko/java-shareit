package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findByOwnerId(Long ownerId);

	List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

	@Query("""
			SELECT i
			FROM Item AS i
			WHERE (upper(i.name) LIKE upper(concat('%', ?1, '%'))
			   OR upper(i.description) LIKE upper(concat('%', ?1, '%')))
			AND i.available = true
			""")
	List<Item> search(String text);
}