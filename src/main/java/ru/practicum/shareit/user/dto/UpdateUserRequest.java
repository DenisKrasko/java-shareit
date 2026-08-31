package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequest {
	private Long id;
	private String name;
	@Email
	private String email;

	public boolean hasEmail() {
		return ! (email == null || email.isBlank());
	}

	public boolean hasName() {
		return ! (name == null || name.isBlank());
	}
}
