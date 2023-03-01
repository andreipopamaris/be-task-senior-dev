package com.amaris.task.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum TaskManagerErrorCode {

    DUE_DATE_MUST_NOT_BE_NULL(HttpStatus.BAD_REQUEST, "ERR-001", "Due Date Must not be null or empity"),
    DESCRIPTION_MUST_NOT_BE_NULL(HttpStatus.BAD_REQUEST, "ERR-002", "Description Must not be null or empity"),
    ASSIGNEE_MUST_NOT_BE_NULL(HttpStatus.BAD_REQUEST, "ERR-003", "Assignee Must not be null!"),
    STATUS_MUST_NOT_BE_NULL(HttpStatus.BAD_REQUEST, "ERR-004", "Status can not be null!"),
    ASSIGNEE_IS_MANDATORY(HttpStatus.BAD_REQUEST, "ERR-005", "Employee Id (Assignee) is Mandatory, can not be null"),

    TASK_ID_IS_MANDATORY(HttpStatus.BAD_REQUEST, "ERR-006", "Task Id is Mandatory, can not be null"),
    NO_TASK_FOUND(HttpStatus.NOT_FOUND, "ERR-007", "No task is found "),
    NO_TASK_IS_FOUND_BY_ID(HttpStatus.NOT_FOUND, "ERR-008", "No task is found with id equals :"),
    FAILED_TO_UPDATE_TASK(HttpStatus.BAD_REQUEST, "ERR-009", "Failed to update Task, No Task with Id "),
    REFERENTIAL_INTEGRITY_CONSTRAINT(HttpStatus.BAD_REQUEST, "ERR-010", "Referential integrity constraint violation, No Employee with Id "),

    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR-0011", "No employee found with id equals "),
    EMPLOYEE_NAME_MUST_NOT_NULL(HttpStatus.NOT_FOUND, "ERR-0012", "Employee Name Must Not be Null!"),
    NO_EMPLOYEE_FOUND(HttpStatus.NOT_FOUND, "ERR-0013", "No Employee Found");


    private HttpStatus httpStatus;
    private String errorCode;
    private String message;
}
