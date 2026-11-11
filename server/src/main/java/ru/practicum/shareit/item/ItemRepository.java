package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
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

	@Query("""
			SELECT i
			FROM Item AS i
			WHERE i.request.id IN :requestIds
			ORDER BY i.id
			""")
	List<Item> findAllByRequestIds(@Param("requestIds") Collection<Long> requestIds);
}
