package com.amaris.task.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.amaris.task.entity.EmployeeEntity;
import com.amaris.task.model.Employee;
import com.amaris.task.repository.EmployeeRepository;
import com.amaris.task.service.impl.CrudEmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
class CrudEmployeeServiceTest {
	@Mock
	private EmployeeRepository employeeRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private CrudEmployeeServiceImpl crudEmployeeService;

	private static EmployeeEntity employeeEntityOne;
	private static EmployeeEntity employeeEntityTwo;
	private static EmployeeEntity employeeEntityThree;
	private static EmployeeEntity employeeEntityFour;
	private static List<EmployeeEntity> employeeEntity;
	
	private static Employee employeeModelOne;
	private static Employee employeeModelTwo;
	private static Employee employeeModelThree;
	private static Employee employeeModelFour;
	
	@BeforeAll
	public static void setupData() {
		employeeEntityOne = EmployeeEntity.builder()
				.id(1L)
				.name("Pino")
				.build();
		employeeEntityTwo = EmployeeEntity.builder()
				.id(2L)
				.name("Nino")
				.build();
		employeeEntityThree = EmployeeEntity.builder()
				.id(3L)
				.name("Rino")
				.build();
		employeeEntityFour = EmployeeEntity.builder()
				.id(4L)
				.name("Lino")
				.build();
		employeeEntity = Arrays.asList(
				employeeEntityOne, 
				employeeEntityTwo, 
				employeeEntityThree, 
				employeeEntityFour
			);

		
		employeeModelOne = Employee.builder()
			.id(1L)
			.name("Pino")
			.build();
		employeeModelTwo = Employee.builder()
				.id(2L)
				.name("Nino")
				.build();
		employeeModelThree = Employee.builder()
				.id(3L)
				.name("Rino")
				.build();
		employeeModelFour = Employee.builder()
				.id(4L)
				.name("Lino")
				.build();
	}
	
	@Test
	@DisplayName(value = "ShouldReturnListOfEmployees")
	void getAllEmployeesTest() {
		Mockito.when( this.employeeRepository.findAllAsStream())
			.thenReturn(employeeEntity.stream());
		Mockito.when( this.modelMapper.map(employeeEntityOne, Employee.class) )
			.thenReturn(employeeModelOne);
		Mockito.when( this.modelMapper.map(employeeEntityTwo, Employee.class) )
			.thenReturn(employeeModelTwo);
		Mockito.when( this.modelMapper.map(employeeEntityThree, Employee.class) )
			.thenReturn(employeeModelThree);
		Mockito.when( this.modelMapper.map(employeeEntityFour, Employee.class) )
			.thenReturn(employeeModelFour);
		
		final List<Employee> tasks = this.crudEmployeeService.getAll();
		
		Assertions.assertThat(tasks).isNotNull();
		Assertions.assertThat(tasks.size()).isEqualTo(4);
	}
	
	@Test
	@DisplayName(value = "ShouldReturnEmployeeByItsId")
	void getEmployeeByIdTest() {
		final Long employeeId = Mockito.anyLong();
		Mockito.when( this.employeeRepository.findById(employeeId))
			.thenReturn(Optional.of(employeeEntityOne));
		Mockito.when( this.modelMapper.map(employeeEntityOne, Employee.class) )
			.thenReturn(employeeModelOne);
		
		final Optional<Employee> employee = this.crudEmployeeService.getById(employeeId);
		
		Assertions.assertThat(employee).isPresent();
		Assertions.assertThat(employee.get().getId()).isEqualTo(1L);
		Assertions.assertThat(employee.get().getName()).isEqualTo("Pino");
	}
	
	// TODO: adding other tests for crudEmployeeService
}