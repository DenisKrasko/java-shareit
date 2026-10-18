package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findAllByItemId(Long itemId);

	@Query("""
        SELECT c
        FROM Comment AS c
        LEFT JOIN FETCH c.author
        WHERE c.item.owner.id = ?1
        ORDER BY c.created
        """)
	List<Comment> findWithAuthorByOwnerId(Long ownerId);
}