package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

	@Query("""
	       SELECT i
	       FROM Item AS i
	       WHERE upper(i.name) LIKE upper(concat('%', ?1, '%'))
	          OR upper(i.description) LIKE upper(concat('%', ?1, '%'))
	       """)
	List<Item> search(String text);
}