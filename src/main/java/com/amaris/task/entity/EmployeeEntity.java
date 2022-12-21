package com.amaris.task.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NAME", length = 255, nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<TaskEntity> tasks;
}