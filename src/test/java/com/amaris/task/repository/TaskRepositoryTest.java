package com.amaris.task.repository;

import com.amaris.task.entity.Status;
import com.amaris.task.entity.Task;
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

import static org.junit.Assert.assertEquals;

@DataR2dbcTest
@DirtiesContext
@NoArgsConstructor
@RunWith(SpringRunner.class)
public class TaskRepositoryTest extends CommonRepositoryOps {

    private static final Logger logger = LogManager.getLogger(TaskRepositoryTest.class);

    @Test
    public void getAllSavedEmployees() {
        StepVerifier.create(employeeRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getAllTasks() {
        StepVerifier.create(taskRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getTaskById() {
        Task task = new Task(1,
                "Task One",
                String.valueOf("27/02/2023"),
                2,
                Status.ASSIGNED
        );

        StepVerifier.create(taskRepository.findById(1)
                .subscribeOn(Schedulers.parallel())).consumeNextWith(actual ->
                assertEquals(actual, task)
        ).verifyComplete();
    }

    @Test
    public void findEmpByIdNotExist() {
        StepVerifier.create(taskRepository.findById(5)
                .subscribeOn(Schedulers.parallel()))
                .expectNextCount(0).verifyComplete();
    }
}
