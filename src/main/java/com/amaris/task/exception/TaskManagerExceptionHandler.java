package com.amaris.task.exception;

import com.amaris.task.common.response.ErrorModel;
import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.common.response.ResponseStatus;
import com.amaris.task.entity.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class TaskManagerExceptionHandler {

    @ExceptionHandler(TaskManagerException.class)
    protected ResponseEntity handleTaskException(TaskManagerException e) {
        ResponseModel<Task> responseModel = new ResponseModel<>();
        responseModel.setStatus(ResponseStatus.KO);
        responseModel.setErrors(new ErrorModel(e.getErrorCode(), e.getErrorMessage()));

        return new ResponseEntity<>(responseModel, e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity handleTaskException(MethodArgumentNotValidException e) {
        ResponseModel<Task> responseModel = new ResponseModel<>();
        responseModel.setStatus(ResponseStatus.KO);

        if (e.getMessage().contains("Due Date Must not be null or")) {
            responseModel.setErrors(
                    new ErrorModel(
                            TaskManagerErrorCode.DUE_DATE_MUST_NOT_BE_NULL.getErrorCode(),
                            TaskManagerErrorCode.DUE_DATE_MUST_NOT_BE_NULL.getMessage()
                    )
            );
        } else if (e.getMessage().contains("Description Must not be null or empty !")) {
            responseModel.setErrors(
                    new ErrorModel(
                            TaskManagerErrorCode.DESCRIPTION_MUST_NOT_BE_NULL.getErrorCode(),
                            TaskManagerErrorCode.DESCRIPTION_MUST_NOT_BE_NULL.getMessage()
                    )
            );
        } else if (e.getMessage().contains("Assignee Must not be null!")) {
            responseModel.setErrors(
                    new ErrorModel(
                            TaskManagerErrorCode.ASSIGNEE_MUST_NOT_BE_NULL.getErrorCode(),
                            TaskManagerErrorCode.ASSIGNEE_MUST_NOT_BE_NULL.getMessage()
                    )
            );
        } else if (e.getMessage().contains("Status can not be null!")) {
            responseModel.setErrors(
                    new ErrorModel(
                            TaskManagerErrorCode.STATUS_MUST_NOT_BE_NULL.getErrorCode(),
                            TaskManagerErrorCode.STATUS_MUST_NOT_BE_NULL.getMessage()
                    )
            );
        } else if (e.getMessage().contains("Employee Name Must Not be Null")) {
            responseModel.setErrors(
                    new ErrorModel(
                            TaskManagerErrorCode.EMPLOYEE_NAME_MUST_NOT_NULL.getErrorCode(),
                            TaskManagerErrorCode.EMPLOYEE_NAME_MUST_NOT_NULL.getMessage()
                    )
            );
        }else {
            responseModel.setErrors(new ErrorModel("ERR-CUS-001", e.getMessage()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
    }
}
