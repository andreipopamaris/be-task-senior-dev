package com.amaris.task.exception;

import com.amaris.task.common.ResponseModel;
import com.amaris.task.common.ResponseStatus;
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
        responseModel.setErrors(e.getErrors().toString());

        return new ResponseEntity<>(responseModel, e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity handleTaskException(MethodArgumentNotValidException e) {
        ResponseModel<Task> responseModel = new ResponseModel<>();
        responseModel.setStatus(ResponseStatus.KO);
        responseModel.setErrors(e.getMessage());

        return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
    }
}
