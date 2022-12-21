package com.amaris.task.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DESCRIPTION", length = 255, nullable = false)
	private String description;
	
	@Column(name = "DUE_DATE")
	@Temporal(value = TemporalType.DATE)
	private Date dueDate;
	
	@ManyToOne
	@JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")
	@JsonManagedReference
	private EmployeeEntity assignee;
	
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
	private Status statusTask;
	
	public enum Status {
		ASSIGNED,
		UNASSIGNED,
		REASSIGNED
	}
}