package com.amaris.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Employee {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    @Column(name = "id")
    @org.springframework.data.annotation.Id
    //@NotNull
    private Integer id;
    @Column(name = "name")
    @NotNull
    private String name;
}
