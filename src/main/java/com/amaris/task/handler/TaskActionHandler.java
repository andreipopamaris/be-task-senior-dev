package com.amaris.task.handler;

import javax.validation.constraints.NotNull;

import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;
import com.amaris.task.service.impl.TaskActionServiceImpl;

import lombok.Data;

@Data
public abstract class TaskActionHandler {
	@NotNull(message = "Task id must be not null")
	public Long taskId;
	
	public Long employeeId;
	
	public TaskActionServiceImpl taskActionService;
	
	public abstract void handleAction(final Task task, final Employee employee);
}