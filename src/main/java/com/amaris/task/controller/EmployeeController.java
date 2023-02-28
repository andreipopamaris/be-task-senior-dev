package com.amaris.task.controller;

import com.amaris.task.common.ResponseModel;
import com.amaris.task.common.ResponseStatus;
import com.amaris.task.entity.Employee;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/app/health")
    public Flux<String> health() {
        return Flux.just("Health check pass success");
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ResponseModel<Employee>>> addEmployee(@Valid @RequestBody Employee employee) {
        return this.employeeService.saveEmployee(Employee.builder().name(employee.getName()).build()).flatMap(emp -> {

            ResponseModel<Employee> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(emp));
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ResponseModel<Employee>>> getEmployee() {
        return this.employeeService.getAllEmployee().flatMap(Flux::just).collectList().flatMap(allEmp -> {
            ResponseModel<Employee> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(allEmp);
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @GetMapping("/{employeeId}")
    public Mono<ResponseEntity<ResponseModel<Employee>>> getEmployeeById(@PathVariable("employeeId") Integer employeeId) {
        return this.employeeService.getEmployeeById(employeeId).flatMap(employees -> {
            if (Objects.isNull(employees.getId()) || Objects.isNull(employees.getName())) {
                throw new TaskManagerException(HttpStatus.NOT_FOUND, "No employee found with id equals :" + employeeId);
            }
            ResponseModel<Employee> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(employees));
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }
}
