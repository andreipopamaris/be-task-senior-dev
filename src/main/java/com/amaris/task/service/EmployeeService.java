package com.amaris.task.service;

import com.amaris.task.entity.Employee;
import com.amaris.task.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Mono<Employee> saveEmployee(Employee employee) {
        return this.employeeRepository.save(employee).flatMap(Mono::just);
    }

    public Flux<Employee> getAllEmployee() {
        return this.employeeRepository.findAll().flatMap(Flux::just);
    }

    public Mono<Void> deleteAllEmployee() {
        return this.employeeRepository.deleteAll().flatMap(Mono::just);
    }

    public Mono<Employee> getEmployeeById(Integer empId) {
        return this.employeeRepository.findById(empId).flatMap(Mono::just).defaultIfEmpty(Employee.builder().build());
    }
}
