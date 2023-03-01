package com.amaris.task.controller;

import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.common.response.ResponseStatus;
import com.amaris.task.dto.EmployeeDto;
import com.amaris.task.entity.Employee;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> addEmployee(@Valid @RequestBody EmployeeDto employee) {
        return this.employeeService.saveEmployee(EmployeeDto.builder().name(employee.getName()).build()).flatMap(emp -> {

            ResponseModel<EmployeeDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(EmployeeDto.builder()
                    .id(emp.getId())
                    .name(emp.getName())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> getEmployee() {
        return this.employeeService.getAllEmployee().flatMap(Flux::just).collectList().flatMap(allEmp -> {

            if (Objects.isNull(allEmp.get(0).getId()) || allEmp.isEmpty()) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.NO_EMPLOYEE_FOUND;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage());
            }

            List<EmployeeDto> employeeDtoList = new ArrayList<>();
            for (Employee employee : allEmp) {
                EmployeeDto employeeDto = new EmployeeDto();
                employeeDto.setId(employee.getId());
                employeeDto.setName(employee.getName());
                employeeDtoList.add(employeeDto);
            }

            ResponseModel<EmployeeDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(employeeDtoList);
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @GetMapping("/{employeeId}")
    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> getEmployeeById(@PathVariable("employeeId") Integer employeeId) {
        return this.employeeService.getEmployeeById(employeeId).flatMap(employees -> {

            if (Objects.isNull(employees.getId()) || Objects.isNull(employees.getName())) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.EMPLOYEE_NOT_FOUND;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + employeeId);
            }
            ResponseModel<EmployeeDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(EmployeeDto.builder()
                    .id(employees.getId())
                    .name(employees.getName())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }
}
