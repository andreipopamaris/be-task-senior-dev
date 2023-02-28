package com.amaris.task.repository;

import com.amaris.task.entity.Employee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EmployeeRepository extends R2dbcRepository<Employee, Integer> {

    @Query("SELECT * FROM employee WHERE name = $1")
    Flux<Employee> findAllEmployeeByName(@Param("name") String name);

}
