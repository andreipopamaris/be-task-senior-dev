package com.amaris.task.common;

import com.amaris.task.dto.EmployeeDto;
import com.amaris.task.dto.TaskDto;
import com.amaris.task.entity.Status;

import java.util.Arrays;
import java.util.List;

public class CommonOps {

    protected List<EmployeeDto> employeeList = Arrays.asList(
            new EmployeeDto(null, "Employee One"),
            new EmployeeDto(null, "Employee Two"),
            new EmployeeDto(null, "Employee Three")
    );

    protected List<TaskDto> taskList = Arrays.asList(
            new TaskDto(null, "Task One", String.valueOf("27/02/2023"), 2, Status.ASSIGNED),
            new TaskDto(null, "Task Two", String.valueOf("03/03/2023"), 2, Status.ASSIGNED),
            new TaskDto(null, "Task Three", String.valueOf("09/03/2023"), 4, Status.ASSIGNED)
    );
}
