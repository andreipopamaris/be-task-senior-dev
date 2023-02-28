package com.amaris.task.repository;

import com.amaris.task.entity.Employee;
import com.amaris.task.entity.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

public class CommonRepositoryOps {
    private static final Logger logger = LogManager.getLogger(CommonRepositoryOps.class);

    private static boolean setuped = false;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    List<Employee> employeeList = Arrays.asList(
            new Employee(null, "Henok"),
            new Employee(null, "Roberta"),
            new Employee(null, "Brook")
    );

    List<Task> taskList = Arrays.asList(
            new Task(null, "Integrate software components and third-party programs", "27/02/2023", 2),
            new Task(null, "Work on the Analysis and Design", "03/03/2023", 2),
            new Task(null, "Write the Codes for the finalized Analysis", "09/03/2023", 4)
    );

    @Before
    public void setUp() {
        if (!setuped) {
            employeeRepository.deleteAll()
                    .thenMany(Flux.fromIterable(employeeList))
                    .flatMap(employeeRepository::save)
                    .doOnNext((employee -> {
                        logger.info("Inserted Emp: " + employee);
                    }))
                    .blockLast();

            taskRepository.deleteAll()
                    .thenMany(Flux.fromIterable(taskList))
                    .flatMap(taskRepository::save)
                    .doOnNext((task -> {
                        logger.info("Inserted Task: " + task);
                    }))
                    .blockLast();

            setuped = true;
        }
    }
}
