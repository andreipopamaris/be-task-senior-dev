package com.amaris.task.controller;

import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.dto.EmployeeDto;
import com.amaris.task.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> addEmployee(@Valid @RequestBody EmployeeDto employee) {
        return this.employeeService.saveEmployee(EmployeeDto.builder().name(employee.getName()).build()).flatMap(Mono::just);
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> getEmployee() {
        return this.employeeService.getAllEmployee().flatMap(Mono::just);
    }

    @GetMapping("/{employeeId}")
    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> getEmployeeById(@PathVariable("employeeId") Integer employeeId) {
        return this.employeeService.getEmployeeById(employeeId).flatMap(Mono::just);
    }
}
