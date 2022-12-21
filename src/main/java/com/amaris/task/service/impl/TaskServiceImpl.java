package com.amaris.task.service.impl;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.amaris.task.exception.ResourceNotFoundException;
import com.amaris.task.handler.TaskActionHandler;
import com.amaris.task.handler.UnassignmentTaskHandler;
import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;
import com.amaris.task.repository.TaskRepository;
import com.amaris.task.service.CrudEmployeeService;
import com.amaris.task.service.TaskService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Validated
@Slf4j
public class TaskServiceImpl extends CrudTaskServiceImpl implements TaskService {
	private final CrudEmployeeService crudEmployeeService;
	
	public TaskServiceImpl(
			TaskRepository taskRepository,
			ModelMapper modelMapper, 
			CrudEmployeeService crudEmployeeService) {
		super(taskRepository, modelMapper);
		this.crudEmployeeService = crudEmployeeService;
	}

	@Override
	public void changeDueDate(@NotNull final Long taskId, @NotNull @Future final Date dueDate) {
		final String errorMessage = String.format("Error to change task due date. Task With id: %s not exists", taskId);
		final Task task = this.getById(taskId)
				.orElseThrow( () -> new ResourceNotFoundException(errorMessage) );

		task.setDueDate(dueDate);
		this.update(taskId, task);
	}

	@Override
	public void manageTask(@NotNull @Valid TaskActionHandler taskActionHandler) {
		log.info("manageTask START - args=[taskActionHandler={}]", taskActionHandler);
		final Long taskId = taskActionHandler.getTaskId();
		final Task task = this.getById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Task With id: %s not exists", taskId)));

		Employee employee = null;
		if( !(taskActionHandler instanceof UnassignmentTaskHandler) ) {
			final Long employeeId = taskActionHandler.getEmployeeId();
			employee = this.crudEmployeeService
					.getById(employeeId)
					.orElseThrow(() -> new ResourceNotFoundException(String.format("Employee With id: %s not exists", employeeId)));
		}

		taskActionHandler.handleAction(task, employee);
	}
}