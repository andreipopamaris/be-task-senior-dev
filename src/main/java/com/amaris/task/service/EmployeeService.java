package com.amaris.task.service;

import com.amaris.task.common.response.ErrorModel;
import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.common.response.ResponseStatus;
import com.amaris.task.dto.EmployeeDto;
import com.amaris.task.entity.Employee;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> saveEmployee(EmployeeDto employee) {
        return this.employeeRepository.save(Employee.builder().name(employee.getName()).build())
                .flatMap(emp -> {
                    ResponseModel<EmployeeDto> responseModel = new ResponseModel<>();
                    responseModel.setStatus(ResponseStatus.OK);
                    responseModel.setPayload(Collections.singletonList(EmployeeDto.builder()
                            .id(emp.getId())
                            .name(emp.getName())
                            .build()));
                    responseModel.setErrors(null);

                    return Mono.just(ResponseEntity.ok(responseModel));
                }).onErrorMap(throwable -> {
                    throw new TaskManagerException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR-CUS-004", throwable.getMessage());
                });
    }

    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> getAllEmployee() {
        return this.employeeRepository.findAll().flatMap(Flux::just).collectList().flatMap(allEmp -> {
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

    public Mono<ResponseEntity<ResponseModel<EmployeeDto>>> getEmployeeById(Integer empId) {
        return this.employeeRepository.findById(empId).flatMap(emp -> {

            ResponseModel<EmployeeDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(EmployeeDto.builder()
                    .id(emp.getId())
                    .name(emp.getName())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        }).defaultIfEmpty(ResponseEntity.ok(
                new ResponseModel<EmployeeDto>(
                        ResponseStatus.KO,
                        null,
                        new ErrorModel(TaskManagerErrorCode.EMPLOYEE_NOT_FOUND.getErrorCode(),
                                TaskManagerErrorCode.EMPLOYEE_NOT_FOUND.getMessage() + empId)
                )
        ));
    }

    public Mono<Void> deleteAllEmployee() {
        return this.employeeRepository.deleteAll().flatMap(Mono::just);
    }
}
