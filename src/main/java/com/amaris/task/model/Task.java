package com.amaris.task.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
	private Long id;
	private String description;
	private Status status;
	private Employee assignee;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date dueDate;
	
	public static enum Status {
		ASSIGNED,
		UNASSIGNED,
		REASSIGNED
	}
}