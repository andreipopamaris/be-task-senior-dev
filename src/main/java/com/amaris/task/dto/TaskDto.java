package com.amaris.task.dto;

import com.amaris.task.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Integer id;
    @NotBlank(message = "Description Must not be null or empty !")
    private String description;
    @NotBlank(message = "Due Date Must not be null or empty !")
    private String dueDate;
    @NotNull(message = "Assignee Must not be null!")
    private Integer assignee;
    private Status status;
}
