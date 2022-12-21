package com.amaris.task.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import com.amaris.task.model.Employee;
import com.amaris.task.service.CrudEmployeeService;
import com.amaris.task.service.TaskService;
import com.amaris.task.service.impl.TaskActionServiceImpl;

@WebMvcTest
class EmployeeControllerTest {
	private static final String BASE_URI = "http://localhost:8080";
	private final static List<Employee> employee = new ArrayList<>();

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private CrudEmployeeService crudEmployeeService;
	@MockBean
	private TaskService taskService;
	@MockBean
	private TaskActionServiceImpl taskActionService;
	
	@BeforeAll
	static void setupData() {
		final Employee employeeModelOne = Employee.builder()
			.id(1L)
			.name("Maria")
			.build();
		final Employee employeeModeTwo = Employee.builder()
				.id(2L)
				.name("Stella")
				.build();
		final Employee emloyeeModelThree = Employee.builder()
				.id(3L)
				.name("Bruno")
				.build();
		employee.add(employeeModelOne);
		employee.add(employeeModeTwo);
		employee.add(emloyeeModelThree);
	}
	
	@Test
	@DisplayName(value = "shouldReturnListOfEmployee")
	void getAllEmployeeTest() throws Exception {
		Mockito.when(this.crudEmployeeService.getAll())
		   .thenReturn(employee);
		
		final ResultActions response = mockMvc.perform(
				 get(BASE_URI.concat("/employee"))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
			);
		
		Mockito.verify(crudEmployeeService, times(1))
			   .getAll();
		
		response
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()",  is(employee.size())))
			.andDo(print());
	}
}