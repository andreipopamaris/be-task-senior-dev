package com.amaris.task.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.amaris.task.entity.TaskEntity;
import com.amaris.task.entity.TaskEntity.Status;
import com.amaris.task.exception.ResponseError;
import com.amaris.task.model.Task;
import com.amaris.task.repository.TaskRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TaskIntegrationTest {
	private String baseUri= "http://localhost:";
	private static final String CONTEXT_PATH = "/tasks";
	
	@LocalServerPort
	private int port;

	private TestRestTemplate testRestTemplate = new TestRestTemplate();

	@Autowired
	private TaskRepository taskRepository;

	@BeforeEach
	void setupUri() {
		baseUri = baseUri.concat(String.valueOf(port)).concat(CONTEXT_PATH);
	}
	
	@Test
	@DisplayName(value = "ShouldReturnResponseOKWithListOfTasks")
	@Sql(statements = ""
		+ "DELETE FROM task;"
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(1, 'Bob'); "
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(2, 'Dan'); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(1, 'Create Service A', 1, 'ASSIGNED', CURRENT_DATE); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(2, 'Create Service B', 1, 'ASSIGNED', CURRENT_DATE); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(3, 'Create Service C', 2, 'ASSIGNED', CURRENT_DATE); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(4, 'Create Service D', 2, 'ASSIGNED', CURRENT_DATE); "
	)
	void getAllNoEmptyTasksListTest() {	
		final ResponseEntity<List<Task>> response = this.testRestTemplate.exchange(
			 baseUri,
			 HttpMethod.GET, 
			 null, 
			 new ParameterizedTypeReference<List<Task>>() {}
		 );
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().size()).isEqualTo(4);
	}
	
	@Test
	@DisplayName(value = "ShouldReturnResponseOKWithEmptyListOfTasks")
	@Sql(statements = "DELETE FROM TASK")
	void getAllEmptyTasksListTest() {
		this.taskRepository.deleteAll();
		final ResponseEntity<List<Task>> response = this.testRestTemplate.exchange(
			 baseUri,
			 HttpMethod.GET, 
			 null, 
			 new ParameterizedTypeReference<List<Task>>() {}
		 );
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().size()).isEqualTo(0);
	}
	
	@Test
	@DisplayName(value = "ShouldManagingAssignimentTask")
	@Sql(statements = ""
		+ "DELETE FROM task;"
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(90, 'Phil'); "
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(91, 'Jim'); "
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(92, 'Joyce'); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(311, 'Create Controller A', 90, 'ASSIGNED', CURRENT_DATE); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(312, 'Create Controller B', 92, 'ASSIGNED', CURRENT_DATE); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(313, 'Create Controller C', NULL, 'UNASSIGNED', NULL); "
	)
	void manageTaskWithAssignmentTest() {	
		final String  taskId = "313";
		final String  employeeId = "91";
		ResponseEntity<ResponseError> response = this.testRestTemplate.exchange(
			 baseUri.concat(String.format("/manage?action=ASSIGNMENT&taskId=%s&employeeId=%s", taskId, employeeId)),
			 HttpMethod.GET, 
			 null, 
			 ResponseError.class
		 );
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		Assertions.assertThat(response.getBody()).isNull();
		
		final TaskEntity task = this.taskRepository.findById(Long.parseLong(taskId)).get();
		Assertions.assertThat(task.getStatusTask()).isEqualTo(Status.ASSIGNED);
		Assertions.assertThat(task.getAssignee().getId()).isEqualTo(Long.parseLong(employeeId));
		Assertions.assertThat(task.getAssignee().getName()).isEqualTo("Jim");
	}
	
	@Test
	@DisplayName(value = "ShouldManagingAssignimentTaskKOForTaskNotExists")
	@Sql(statements = "DELETE FROM TASK")
	void manageTaskWithAssignmentKOForTaskNotExistsTest() {	
		final String  taskId = "1000";
		ResponseEntity<ResponseError> response = this.testRestTemplate.exchange(
			 baseUri.concat(String.format("/manage?action=ASSIGNMENT&taskId=%s&employeeId=1", taskId)),
			 HttpMethod.GET, 
			 null, 
			 ResponseError.class
		 );
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().getPath()
				  .contains(String.format("/manage?action=ASSIGNMENT&taskId=%s&employeeId=1", taskId)));
		Assertions.assertThat(response.getBody().getErrorMessage())
				  .isEqualTo(String.format("Task With id: %s not exists", Long.parseLong(taskId)));
	}
	
	@Test
	@DisplayName(value = "ShouldManagingAssignimentTaskKOForEmployeeNotExists")
	@Sql(statements = ""
		+ "DELETE FROM task;"
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(1, 'Create Service A', NULL, 'UNASSIGNED', NULL); ",
		executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
	)
	void manageTaskWithAssignmentKOForEmployeeNotExistsTest() {	
		final String taskId = "1";
		final String employeeId = "10000";
		ResponseEntity<ResponseError> response = this.testRestTemplate.exchange(
			 baseUri.concat(String.format("/manage?action=ASSIGNMENT&taskId=%s&employeeId=%s", taskId, employeeId)),
			 HttpMethod.GET, 
			 null, 
			 ResponseError.class
		 );
		
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().getPath()
				  .contains(String.format("/manage?action=ASSIGNMENT&taskId=%s&employeeId=%s", taskId, employeeId)));
		Assertions.assertThat(response.getBody().getErrorMessage())
				  .isEqualTo(String.format("Employee With id: %s not exists", employeeId));
	}
	
	@Test
	@DisplayName(value = "ShouldManagingUnassignimentTaskKOForTaskNotAssigned")
	@Sql(statements = ""
		+ "DELETE FROM task;"
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(100, 'Create Service A', 1, 'UNASSIGNED', NULL); "
	)
	void manageTaskWithUnassignmentKOForTaskNotAssignedTest() {	
		final Long  taskId = 100L;
		ResponseEntity<ResponseError> response = this.testRestTemplate.exchange(
			 baseUri.concat(String.format("/manage?action=UNASSIGNMENT&taskId=%s&employeeId=1", taskId.toString())),
			 HttpMethod.GET, 
			 null, 
			 ResponseError.class
		 );

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		Assertions.assertThat(response.getBody()).isNotNull();
		Assertions.assertThat(response.getBody().getPath()
				  .contains(String.format("/manage?action=ASSIGNMENT&taskId=%s&employeeId=1", taskId)));
		Assertions.assertThat(response.getBody().getErrorMessage())
				  .isEqualTo(String.format("Task with id: %s cannot be unassignable because is already unassigned.", taskId));
	}
}