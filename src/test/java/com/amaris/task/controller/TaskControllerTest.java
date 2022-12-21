package com.amaris.task.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.amaris.task.handler.TaskActionHandler;
import com.amaris.task.handler.TaskActionHandlerFactory;
import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;
import com.amaris.task.model.Task.Status;
import com.amaris.task.model.TaskAction;
import com.amaris.task.service.CrudEmployeeService;
import com.amaris.task.service.TaskService;
import com.amaris.task.service.impl.TaskActionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
class TaskControllerTest {
	private static final String BASE_URI = "http://localhost:8080";
	private final static List<Task> tasks = new ArrayList<>();

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private CrudEmployeeService crudEmployeeService;
	@MockBean
	private TaskService taskService;
	@MockBean
	private TaskActionServiceImpl taskActionService;
	
	@BeforeAll
	static void setupData() {
		final Task taskModelOne = Task.builder()
			.id(1L)
			.description("Task A")
			.assignee(Employee.builder().id(1L).name("Ciccio").build())
			.status(Status.ASSIGNED)
			.dueDate(new Date())
			.build();
		final Task taskModelTwo = Task.builder()
				.id(2L)
				.description("Task B")
				.assignee(null)
				.status(Status.UNASSIGNED)
				.dueDate(null)
				.build();
		final Task taskModelThree = Task.builder()
				.id(3L)
				.description("Task C")
				.assignee(Employee.builder().id(3L).name("Tony").build())
				.status(Status.REASSIGNED)
				.dueDate(new Date())
				.build();
		tasks.add(taskModelOne);
		tasks.add(taskModelTwo);
		tasks.add(taskModelThree);
	}
	
	@Test
	@DisplayName(value = "shouldReturnListOfTask")
	void getAllTaskTest() throws Exception {
		Mockito.when(this.taskService.getAll())
		   .thenReturn(tasks);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
		       .getAll();
		
		response
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()",  is(tasks.size())))
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldReturnTaskByItsId")
	void getTaskByIdTest() throws Exception {
		final Long taskId = Mockito.anyLong();
		final Task task = Task.builder()
				.id(taskId)
				.description("Task A")
				.assignee(Employee.builder().id(1L).name("Ciccio").build())
				.status(Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		
		Mockito.when(this.taskService.getById(taskId))
		   .thenReturn(Optional.of(task));
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/" + taskId))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
		       .getById(taskId);
		
		response
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id",            is(Integer.parseInt(task.getId().toString()))))
			.andExpect(jsonPath("$.description",   is(task.getDescription())))
			.andExpect(jsonPath("$.assignee.id",   is(Integer.parseInt(task.getAssignee().getId().toString()))))
			.andExpect(jsonPath("$.assignee.name", is(task.getAssignee().getName())))
			.andExpect(jsonPath("$.status",        is(task.getStatus().toString())))
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldReturnEmptyResult")
	void getTaskByIdWithEmptyResultTest() throws Exception {
		final Long taskId = Mockito.anyLong();
		final Task task = Task.builder()
				.id(taskId)
				.description("Task A")
				.assignee(Employee.builder().id(1L).name("Ciccio").build())
				.status(Status.ASSIGNED)
				.dueDate(new Date())
				.build();
		
		Mockito.when(this.taskService.getById(taskId))
		   .thenReturn(Optional.empty());
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/" + taskId))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
			   .getById(taskId);
		
		response
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldManageTaskWithAssignmentAction")
	void manageTaskWithAssignmentTest() throws Exception {
		final Long employeeId = 1L;
		final Long taskId = 1L;
		final TaskAction taskAction = TaskAction.ASSIGNMENT;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(taskAction);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(this.taskActionService);

		Mockito.doNothing()
			.when(this.taskService)
			.manageTask(taskActionHandler);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/manage"))
				 .param("action", taskAction.toString())
				 .param("taskId",  String.valueOf(taskId))
				 .param("employeeId", String.valueOf(employeeId))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
			   .manageTask(taskActionHandler);
		
		response
			.andExpect(status().isOk())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldManageTaskWithAssignmentActionWithBadRequestForMissingEmployeeId")
	void manageTaskWithAssignmentFailedForMissingEmployeeIdTest() throws Exception {
		final Long taskId = 1L;
		final Long employeeId = null;
		final TaskAction taskAction = TaskAction.ASSIGNMENT;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(taskAction);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(this.taskActionService);

		Mockito.doNothing()
			.when(this.taskService)
			.manageTask(taskActionHandler);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/manage"))
				 .param("action", taskAction.toString())
				 .param("taskId",  String.valueOf(taskId))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(0))
			   .manageTask(taskActionHandler);
		
		response
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldManageTaskWithUnassignmentAction")
	void manageTaskWithUnassignmentActionTest() throws Exception {
		final Long taskId = 1L;
		final Long employeeId = null;
		final TaskAction taskAction = TaskAction.UNASSIGNMENT;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(taskAction);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(taskActionService);

		Mockito.doNothing()
			.when(this.taskService)
			.manageTask(taskActionHandler);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/manage"))
				 .param("action", taskAction.toString())
				 .param("taskId",  String.valueOf(taskId))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
			   .manageTask(taskActionHandler);
		
		response
			.andExpect(status().isOk())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldManageTaskWithReassignmentAction")
	void manageTaskWithReassignmentActionTest() throws Exception {
		final Long employeeId = 2L;
		final Long taskId = 1L;
		final TaskAction taskAction = TaskAction.REASSIGNMENT;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(taskAction);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(taskActionService);

		Mockito.doNothing()
			.when(this.taskService)
			.manageTask(taskActionHandler);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/manage"))
				 .param("action", taskAction.toString())
				 .param("taskId",  String.valueOf(taskId))
				 .param("employeeId",  String.valueOf(employeeId))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
			   .manageTask(taskActionHandler);
		
		response
			.andExpect(status().isOk())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldManageTaskWithReassignmentActionWithBadRequestForMissingEmployeeId")
	void manageTaskWithReassignmentFailedForMissingEmployeeIdTest() throws Exception {
		final Long taskId = 1L;
		final Long employeeId = null;
		final TaskAction taskAction = TaskAction.REASSIGNMENT;
		final TaskActionHandler taskActionHandler = TaskActionHandlerFactory.of(taskAction);
		taskActionHandler.setTaskId(taskId);
		taskActionHandler.setEmployeeId(employeeId);
		taskActionHandler.setTaskActionService(taskActionService);

		Mockito.doNothing()
			.when(this.taskService)
			.manageTask(taskActionHandler);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/manage"))
				 .param("action", taskAction.toString())
				 .param("taskId",  String.valueOf(taskId))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(0))
			   .manageTask(taskActionHandler);
		
		response
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldChangeDueDateTask")
	void changeDueDateTaskTest() throws Exception {
		final Long taskId = 1L;
		final TaskAction taskAction = TaskAction.ASSIGNMENT;
		final String dateToChangeString = "23-12-2022";
		final Date dateToChange = new SimpleDateFormat("dd-MM-yyyy").parse(dateToChangeString);
		
		Mockito.doNothing()
			.when(this.taskService)
			.changeDueDate(taskId, dateToChange);
		
		final ResultActions response = mockMvc.perform(
				 put(BASE_URI.concat("/" + taskId).concat("/due-date/").concat(dateToChangeString))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(taskService, times(1))
			   .changeDueDate(taskId, dateToChange);
		
		response
			.andExpect(status().isOk())
			.andDo(print());
	}
	
	@Test
	@DisplayName(value = "shouldChangeDueDateTaskBadRequestForChangeDueDateIncorrectFormat")
	void changeDueDateTaskFailedForDuDateToChangeIncorrectFormatTest() throws Exception {
		final Long taskId = 1L;
		final String dateToChangeString = "23 Dicembre 2022";
		
		final ResultActions response = mockMvc.perform(
				 put(BASE_URI.concat("/" + taskId).concat("/due-date/").concat(dateToChangeString))
				 .contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		response
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
}