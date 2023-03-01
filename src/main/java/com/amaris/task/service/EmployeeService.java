package com.amaris.task.service;

import com.amaris.task.dto.EmployeeDto;
import com.amaris.task.entity.Employee;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Mono<Employee> saveEmployee(EmployeeDto employee) {
        return this.employeeRepository.save(Employee.builder().name(employee.getName()).build())
                .flatMap(Mono::just)
                .onErrorMap(throwable -> {
                    throw new TaskManagerException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR-CUS-004", throwable.getMessage());
                });
    }

    public Flux<Employee> getAllEmployee() {
        return this.employeeRepository.findAll().flatMap(Flux::just).defaultIfEmpty(Employee.builder().build());
    }

    public Mono<Employee> getEmployeeById(Integer empId) {
        return this.employeeRepository.findById(empId).flatMap(Mono::just).defaultIfEmpty(Employee.builder().build());
    }

    public Mono<Void> deleteAllEmployee() {
        return this.employeeRepository.deleteAll().flatMap(Mono::just);
    }
}
