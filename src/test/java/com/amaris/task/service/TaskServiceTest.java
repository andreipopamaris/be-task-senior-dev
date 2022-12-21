package com.amaris.task.service;

import static org.mockito.Mockito.times;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.amaris.task.entity.EmployeeEntity;
import com.amaris.task.entity.TaskEntity;
import com.amaris.task.entity.TaskEntity.Status;
import com.amaris.task.exception.ResourceNotFoundException;
import com.amaris.task.handler.TaskActionHandler;
import com.amaris.task.handler.TaskActionHandlerFactory;
import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;
import com.amaris.task.model.TaskAction;
import com.amaris.task.repository.TaskRepository;
import com.amaris.task.service.impl.TaskActionServiceImpl;
import com.amaris.task.service.impl.TaskServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
	@Mock
	private CrudEmployeeService crudEmployeeService;
	@Mock
	private TaskActionServiceImpl taskActionService;
	@Mock
	private TaskRepository taskRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private TaskServiceImpl taskService;
	
	@Test
	@DisplayName(value = "ShouldManageTaskWithAssignmentAction")
	void manageTaskEmployeeSuccessWithAssignmentActionTest() {
		final Long employeeId = 1L;
		final Long taskId = 2L;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(TaskAction.ASSIGNMENT);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(taskActionService);
		final TaskEntity taskEntity = TaskEntity.builder()
				.id(taskId)
				.description("Task One")
				.assignee(null)
				.statusTask(com.amaris.task.entity.TaskEntity.Status.UNASSIGNED)
				.dueDate(new Date())
				.build();
		final Task taskModel = Task.builder()
				.id(taskId)
				.description("Task One")
				.assignee(null)
				.status(com.amaris.task.model.Task.Status.UNASSIGNED)
				.dueDate(new Date())
				.build();
		
		Mockito.when(this.taskRepository.findById(taskId))
			.thenReturn(Optional.of(taskEntity));
		Mockito.when(this.modelMapper.map(taskEntity, Task.class) )
			.thenReturn(taskModel);
		Mockito.when(this.crudEmployeeService.getById(employeeId))
			.thenReturn(
				Optional.of(
					Employee.builder()
						.id(employeeId)
						.name("Bob")
						.build()
				)
			);
		
		this.taskService.manageTask(taskActionHandler);
		
		Mockito.verify(this.taskActionService, times(1))
			   .assignTask(Mockito.any(Task.class), Mockito.any(Employee.class));
	}
	

	@Test
	@DisplayName(value = "ShouldManageTaskWithUnassignmentAction")
	void manageTaskEmployeeSuccessWithUnassignmentActionTest() {
		final Long employeeId = 1L;
		final Long taskId = 2L;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(TaskAction.UNASSIGNMENT);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(null);
		taskActionHandler.setTaskActionService(taskActionService);
		final TaskEntity taskEntity = TaskEntity.builder()
				.id(taskId)
				.description("Task One")
				.assignee( EmployeeEntity.builder().id(employeeId).name("Bob").build() )
				.statusTask(com.amaris.task.entity.TaskEntity.Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		final Task taskModel = Task.builder()
				.id(taskId)
				.description("Task One")
				.assignee(Employee.builder().id(employeeId).name("Bob").build() )
				.status(com.amaris.task.model.Task.Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		
		Mockito.when(this.taskRepository.findById(taskId))
			.thenReturn(Optional.of(taskEntity));
		Mockito.when(this.modelMapper.map(taskEntity, Task.class) )
			.thenReturn(taskModel);
		
		this.taskService.manageTask(taskActionHandler);
		
		Mockito.verify(this.taskActionService, times(1))
			   .unassignTask(Mockito.any(Task.class));
	}
	
	@Test
	@DisplayName(value = "ShouldManageTaskWithReassignmentAction")
	void manageTaskEmployeeSuccessWithReassignmentActionTest() {
		final Long employeeId = 6L;
		final Long taskId = 2L;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(TaskAction.REASSIGNMENT);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(taskActionService);
		final TaskEntity taskEntity = TaskEntity.builder()
				.id(taskId)
				.description("Task One")
				.assignee(EmployeeEntity.builder().id(employeeId).name("Miky").build())
				.statusTask(com.amaris.task.entity.TaskEntity.Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		final Task taskModel = Task.builder()
				.id(taskId)
				.description("Task One")
				.assignee(Employee.builder().id(employeeId).name("Miky").build())
				.status(com.amaris.task.model.Task.Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		
		Mockito.when(this.taskRepository.findById(taskId))
			.thenReturn(Optional.of(taskEntity));
		Mockito.when(this.modelMapper.map(taskEntity, Task.class) )
			.thenReturn(taskModel);
		Mockito.when(this.crudEmployeeService.getById(employeeId))
			.thenReturn(
				Optional.of(
					Employee.builder()
						.id(employeeId)
						.name("Miky")
						.build()
				)
			);
		
		this.taskService.manageTask(taskActionHandler);
		
		Mockito.verify(this.taskActionService, times(1))
			   .reassignTask(Mockito.any(Task.class), Mockito.any(Employee.class));
	}
	
	@Test
	@DisplayName(value = "ShouldChangeDueDateTask")
	void changeDueDateSuccessTest() throws ParseException {
		final Long taskId = 1L;
		final TaskEntity taskEntity = TaskEntity.builder()
			.id(taskId)
			.description("Task One")
			.assignee(EmployeeEntity.builder().id(1L).name("Dino").build())
			.statusTask(Status.ASSIGNED)
			.dueDate(new Date())
			.build();
		final Task taskModel = Task.builder()
				.id(taskId)
				.description("Task One")
				.assignee(Employee.builder().id(1L).name("Dino").build())
				.status(com.amaris.task.model.Task.Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		final Date dateToChange = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-2031");
		final TaskEntity taskUpdated = TaskEntity.builder()
				.id(taskId)
				.description(taskEntity.getDescription())
				.assignee(taskEntity.getAssignee())
				.statusTask(taskEntity.getStatusTask())
				.dueDate(dateToChange)
				.build();
		
		Mockito.when(this.taskRepository.findById(taskId))
				.thenReturn(Optional.of(taskEntity));
		Mockito.when(this.modelMapper.map(taskEntity, Task.class) )
				.thenReturn(taskModel);
		
		Mockito.when(this.taskRepository.findById(taskId))
			   .thenReturn(Optional.of(taskEntity));
		Mockito.when(this.modelMapper.map(taskModel, TaskEntity.class) )
			   .thenReturn(taskUpdated);
		Mockito.when(this.taskRepository.save(taskUpdated) )
			   .thenReturn(taskUpdated);
		
		this.taskService.changeDueDate(taskId, dateToChange);
	}
	
	@Test
	@DisplayName(value = "ShouldNotChangeDueDateTaskForTaskNotExists")
	void changeDueDateFailForTaskNotExistsTest() throws ParseException {
		final Long taskId = 1L;
		final Date dateToChange = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-2031");
		Mockito.when(this.taskRepository.findById(taskId))
				.thenReturn(Optional.empty());
		
		Assertions.assertThrows(
			ResourceNotFoundException.class, 
			() -> this.taskService.changeDueDate(taskId, dateToChange)
		);
	}	
}
