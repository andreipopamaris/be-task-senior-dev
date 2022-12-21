package com.amaris.task.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.amaris.task.entity.TaskEntity;
import com.amaris.task.entity.TaskEntity.Status;

// TODO: to write unit test employeeRepository

@DataJpaTest
@Transactional
class EmployeeRepositoryTest {
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Test
	@Sql(statements = ""
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
	@DisplayName(value = "ShouldReturnListOfTask")
	void getAllTest() {
		final List<TaskEntity> tasks = this.taskRepository.findAll();
		
		Assertions.assertThat(tasks).isNotNull();
		Assertions.assertThat(tasks.size()).isEqualTo(4);
		tasks.forEach( task -> Assertions.assertThat(task).isNotNull() );
		Assertions.assertThat(tasks.get(0).getId()).isEqualTo(1L);
		Assertions.assertThat(tasks.get(0).getAssignee()).isNotNull();
		Assertions.assertThat(tasks.get(1).getId()).isEqualTo(2L);
		Assertions.assertThat(tasks.get(1).getAssignee()).isNotNull();
		Assertions.assertThat(tasks.get(2).getId()).isEqualTo(3L);
		Assertions.assertThat(tasks.get(2).getAssignee()).isNotNull();
		Assertions.assertThat(tasks.get(3).getId()).isEqualTo(4L);
		Assertions.assertThat(tasks.get(3).getAssignee()).isNotNull();
	}
	
	@Test
	@Sql(statements = ""
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(1, 'Bob'); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(1, 'Create Service A', 1, 'ASSIGNED', CURRENT_DATE); "
	)
	@DisplayName(value = "ShouldReturnTaskByItsId")
	void getByIdTest() {
		final Long taskId = 1L;
		final Optional<TaskEntity> task = this.taskRepository.findById(taskId);
		
		Assertions.assertThat(task).isPresent();
		Assertions.assertThat(task.get().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getDescription()).isEqualTo("Create Service A");
		Assertions.assertThat(task.get().getAssignee()).isNotNull();
		Assertions.assertThat(task.get().getAssignee().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getAssignee().getName()).isEqualTo("Bob");
		Assertions.assertThat(task.get().getStatusTask().name()).isEqualTo(Status.ASSIGNED.name());
		Assertions.assertThat(task.get().getDueDate()).isNotNull();
	}
	
	@Test
	@DisplayName(value = "ShouldSaveNewUnassignedTask")
	void saveUnassignedTaskTest() {
		final Date currentDate = new Date();
		final TaskEntity taskUpdated = this.taskRepository.save(
			TaskEntity.builder()
				.description("New Generic Task")
				.assignee(null)
				.statusTask(Status.UNASSIGNED)
				.dueDate(currentDate)
				.build()
		);
		
		Assertions.assertThat(taskUpdated).isNotNull();
		Assertions.assertThat(taskUpdated.getDescription()).isEqualTo("New Generic Task");
		Assertions.assertThat(taskUpdated.getAssignee()).isNull();
		Assertions.assertThat(taskUpdated.getStatusTask().name()).isEqualTo(Status.UNASSIGNED.name());
		Assertions.assertThat(taskUpdated.getDueDate()).isEqualTo(currentDate);
	}
	
	@Test
	@Sql(statements = ""
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(1, 'Bob'); "
	)
	@DisplayName(value = "ShouldSaveNewAssignedTask")
	void saveAssignedTaskTest() {
		final Date currentDate = new Date();
		final TaskEntity task = this.taskRepository.save(
			TaskEntity.builder()
				.description("New Generic Task")
				.assignee(this.employeeRepository.findById(1L).get())
				.statusTask(Status.ASSIGNED)
				.dueDate(currentDate)
				.build()
		);
		
		Assertions.assertThat(task).isNotNull();
		Assertions.assertThat(task.getDescription()).isEqualTo("New Generic Task");
		Assertions.assertThat(task.getAssignee()).isNotNull();
		Assertions.assertThat(task.getAssignee().getId()).isEqualTo(1L);
		Assertions.assertThat(task.getAssignee().getName()).isEqualTo("Bob");
		Assertions.assertThat(task.getStatusTask().name()).isEqualTo(Status.ASSIGNED.name());
		Assertions.assertThat(task.getDueDate()).isEqualTo(currentDate);
	}
	
	@Test
	@Sql(statements = ""
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(1, 'Bob'); "
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(2, 'Dan'); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(1, 'New Generic Task', 1, 'ASSIGNED', NULL); "
	)
	@DisplayName(value = "ShouldUpdateExistingTask")
	void updateTaskTest() {
		final Long taskId = 1L;
		Optional<TaskEntity> task = this.taskRepository.findById(taskId);
		
		Assertions.assertThat(task).isPresent();
		Assertions.assertThat(task.get().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getDescription()).isEqualTo("New Generic Task");
		Assertions.assertThat(task.get().getAssignee()).isNotNull();
		Assertions.assertThat(task.get().getAssignee().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getAssignee().getName()).isEqualTo("Bob");
		Assertions.assertThat(task.get().getStatusTask().name()).isEqualTo(Status.ASSIGNED.name());
		Assertions.assertThat(task.get().getDueDate()).isNull();
		
		final Date currentDate = new Date();
		final TaskEntity taskUpdated = this.taskRepository.save(
			TaskEntity.builder()
				.id(1L)
				.description("New Generic Task update description")
				.assignee(this.employeeRepository.findById(2L).get())
				.statusTask(Status.REASSIGNED)
				.dueDate(currentDate)
				.build()
		);
		
		Assertions.assertThat(taskUpdated).isNotNull();
		Assertions.assertThat(taskUpdated.getId()).isEqualTo(1L);
		Assertions.assertThat(taskUpdated.getDescription()).isEqualTo("New Generic Task update description");
		Assertions.assertThat(taskUpdated.getAssignee()).isNotNull();
		Assertions.assertThat(taskUpdated.getAssignee().getId()).isEqualTo(2L);
		Assertions.assertThat(taskUpdated.getAssignee().getName()).isEqualTo("Dan");
		Assertions.assertThat(taskUpdated.getStatusTask().name()).isEqualTo(Status.REASSIGNED.name());
		Assertions.assertThat(taskUpdated.getDueDate()).isEqualTo(currentDate);
		
		task = this.taskRepository.findById(taskId);
		
		Assertions.assertThat(task).isNotNull();
		Assertions.assertThat(task.get().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getDescription()).isEqualTo("New Generic Task update description");
		Assertions.assertThat(task.get().getAssignee()).isNotNull();
		Assertions.assertThat(task.get().getAssignee().getId()).isEqualTo(2L);
		Assertions.assertThat(task.get().getAssignee().getName()).isEqualTo("Dan");
		Assertions.assertThat(task.get().getStatusTask().name()).isEqualTo(Status.REASSIGNED.name());
		Assertions.assertThat(task.get().getDueDate()).isEqualTo(currentDate);
		Assertions.assertThat(task.get().getDueDate()).isEqualTo(currentDate);
	}
	
	@Test
	@Sql(statements = ""
		+ "INSERT INTO employee(id, name)"
		+ "	VALUES(1, 'Bob'); "
		+ "INSERT INTO task(id, description, employee_id, status, due_date)"
		+ "	VALUES(1, 'Old Task pending', 1, 'ASSIGNED', NULL); "
	)
	@DisplayName(value = "ShouldDeleteExistingTaskByItsId")
	void deleteTaskTest() {
		final Long taskId = 1L;
		Optional<TaskEntity> task = this.taskRepository.findById(taskId);
		
		Assertions.assertThat(task).isPresent();
		Assertions.assertThat(task.get().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getDescription()).isEqualTo("Old Task pending");
		Assertions.assertThat(task.get().getAssignee()).isNotNull();
		Assertions.assertThat(task.get().getAssignee().getId()).isEqualTo(1L);
		Assertions.assertThat(task.get().getAssignee().getName()).isEqualTo("Bob");
		Assertions.assertThat(task.get().getStatusTask().name()).isEqualTo(Status.ASSIGNED.name());
		Assertions.assertThat(task.get().getDueDate()).isNull();
		
		this.taskRepository.deleteById(taskId);
		
		task = this.taskRepository.findById(taskId);
		
		Assertions.assertThat(task).isNotPresent();
	}
}