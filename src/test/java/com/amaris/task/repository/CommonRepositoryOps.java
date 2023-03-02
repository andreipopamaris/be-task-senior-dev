package com.amaris.task.repository;

import com.amaris.task.common.CommonOps;
import com.amaris.task.entity.Employee;
import com.amaris.task.entity.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

public class CommonRepositoryOps extends CommonOps {
    private static final Logger logger = LogManager.getLogger(CommonRepositoryOps.class);

    private static boolean setuped = false;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Before
    public void setUp() {
        if (!setuped) {
            employeeRepository.deleteAll()
                    .thenMany(Flux.fromIterable(employeeList))
                    .flatMap(empDto -> employeeRepository.save(
                            Employee.builder()
                                    .name(empDto.getName())
                                    .build()
                    ))
                    .doOnNext((employee -> {
                        logger.info("Inserted Emp: " + employee);
                    }))
                    .blockLast();

            taskRepository.deleteAll()
                    .thenMany(Flux.fromIterable(taskList))
                    .flatMap(taskDto -> taskRepository.save(Task.builder()
                            .id(taskDto.getId())
                            .description(taskDto.getDescription())
                            .dueDate(taskDto.getDueDate())
                            .assignee(taskDto.getAssignee())
                            .status(taskDto.getStatus())
                            .build()))
                    .doOnNext((task -> {
                        logger.info("Inserted Task: " + task);
                    }))
                    .blockLast();

            setuped = true;
        }
    }
}
