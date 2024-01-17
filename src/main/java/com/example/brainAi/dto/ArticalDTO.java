package com.example.brainAi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@Getter
@Setter
public class ArticalDTO {

    @NotNull(message = "artical id should not be null")
    private Long idArtical;

    @NotNull(message = "artical's description could'nt be null")
    @NotBlank(message = "artical's description could'nt be blank")
    @NotEmpty(message = "artical's description could'nt be empty")
    private String description;

    @NotNull(message = "artical's title could'nt be null")
    @NotBlank(message = "artical's title could'nt be blank")
    @NotEmpty(message = "artical's title could'nt be empty")
    private String title;

    @NotNull(message = "artical's content could'nt be null")
    @NotBlank(message = "artical's description could'nt be blank")
    @NotEmpty(message = "artical's description could'nt be empty")
    private String content;

    @DateTimeFormat
    @NotNull(message = "artical's date couldn't be null")
    private Date date;

}
