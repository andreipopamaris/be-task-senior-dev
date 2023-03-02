package com.amaris.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Task {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    @Column(name = "id")
    @org.springframework.data.annotation.Id
    private Integer id;
    @Column(name = "description")
    @NotBlank(message = "Description Must not be null or empty !")
    private String description;
    @Column(name = "due_date")
    @NotBlank(message = "Due Date Must not be null or empty !")
    private String dueDate;
    @Column(name = "assignee")
    @NotNull(message = "Assignee Must not be null!")
    private Integer assignee;
    @Column(name = "status")
    @NotNull(message = "Status can not be null!")
    private Status status;
}
