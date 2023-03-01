package com.amaris.task.service;

import com.amaris.task.common.CommonOps;
import com.amaris.task.repository.EmployeeRepository;
import com.amaris.task.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

public class CommonServiceOps extends CommonOps {
    private static final Logger logger = LogManager.getLogger(CommonServiceOps.class);

    private static boolean setuped = false;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected EmployeeService employeeService;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected TaskService taskService;

    @Before
    public void setUp() {
        if (!setuped) {
            employeeService.deleteAllEmployee()
                    .thenMany(Flux.fromIterable(employeeList))
                    .flatMap(employeeService::saveEmployee)
                    .doOnNext((employee -> {
                        logger.info("Inserted Emp: " + employee);
                    }))
                    .blockLast();

            taskService.deleteAllTask()
                    .thenMany(Flux.fromIterable(taskList))
                    .flatMap(taskService::assignTask)
                    .doOnNext((task -> {
                        logger.info("Inserted Task: " + task);
                    }))
                    .blockLast();

            setuped = true;
        }
    }
}
