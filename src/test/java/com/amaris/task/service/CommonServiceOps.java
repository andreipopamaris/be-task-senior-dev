package com.amaris.task.service;

import com.amaris.task.entity.Employee;
import com.amaris.task.entity.Task;
import com.amaris.task.repository.EmployeeRepository;
import com.amaris.task.repository.TaskRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

public class CommonServiceOps {
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

    List<Employee> employeeList = Arrays.asList(
            new Employee(null, "Henok"),
            new Employee(null, "Penny"),
            new Employee(null, "Brook")
    );

    List<Task> taskList = Arrays.asList(
            new Task(null, "Task One", "27/02/2023", 2),
            new Task(null, "Task Two", "03/03/2023", 2),
            new Task(null, "Task Three", "09/03/2023", 4)
    );

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
                    .flatMap(taskService::manageTask)
                    .doOnNext((task -> {
                        logger.info("Inserted Task: " + task);
                    }))
                    .blockLast();

            setuped = true;
        }
    }
}
