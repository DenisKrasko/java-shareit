package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequestDto {
	private String description;
	private User requestor;
	private LocalDateTime created;
}
