package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequestDto {

	@NotBlank(message = "описание запроса не может быть пустым")
	@Size(max = 1000, message = "описание не должно быть длиннее 1000 символов")
	private String description;
}
