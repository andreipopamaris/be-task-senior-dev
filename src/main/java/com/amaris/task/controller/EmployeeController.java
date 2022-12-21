package com.amaris.task.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amaris.task.model.Employee;
import com.amaris.task.service.CrudEmployeeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/employee")
@Validated
@Slf4j
public class EmployeeController {
	private final CrudEmployeeService crudEmployeeService;
	
	public EmployeeController(CrudEmployeeService crudEmployeeService) {
		this.crudEmployeeService = crudEmployeeService;
	}
	
	@GetMapping
	public ResponseEntity<List<Employee>> getAllEmployee() {
		log.info("get all employees");
		final List<Employee> tasks = this.crudEmployeeService.getAll();
		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}
}