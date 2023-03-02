package com.amaris.task.repository;

import com.amaris.task.entity.Employee;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;


@DataR2dbcTest
@DirtiesContext
@NoArgsConstructor
@RunWith(SpringRunner.class)
public class EmployeeRepositoryTest extends CommonRepositoryOps {

    private static final Logger logger = LogManager.getLogger(EmployeeRepositoryTest.class);

    @Test
    public void getAllEmployees() {
        StepVerifier.create(employeeRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getEmployeeById() {

        Employee employee = new Employee(2, "Employee One");

        StepVerifier.create(employeeRepository.findById(2)
                .subscribeOn(Schedulers.parallel())).consumeNextWith(actual ->
                assertEquals(actual, employee)
        ).verifyComplete();
    }

    @Test
    public void findEmpByIdNotExist() {
        StepVerifier.create(employeeRepository.findById(1)
                .subscribeOn(Schedulers.parallel()))
                .expectNextCount(0).verifyComplete();
    }
}
