package ru.job4j.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PersonDto {

    @NotNull(message = "Id must be non null")
    private int id;

    @NotBlank(message = "Title must be not empty")
    private String password;

}
