package com.amaris.task.controller;

import com.amaris.task.common.ResponseModel;
import com.amaris.task.common.ResponseStatus;
import com.amaris.task.entity.Task;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/assign")
    public Mono<ResponseEntity<ResponseModel<Task>>> assignTask(@Valid @RequestBody Task task) {
        return this.taskService.manageTask(Task.builder()
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .assignee(task.getAssignee())
                .build()).flatMap(tsk -> {

            ResponseModel<Task> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(tsk));
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @PutMapping("/edit")
    public Mono<ResponseEntity<ResponseModel<Task>>> editTask(@Valid @RequestBody Task task) {
        return manageTask(task).flatMap(Mono::just);
    }

    public Mono<ResponseEntity<ResponseModel<Task>>> manageTask(Task task) {
        return this.taskService.updateTask(Task.builder()
                .id(task.getId())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .assignee(task.getAssignee())
                .build()).flatMap(tsk -> {

            ResponseModel<Task> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(tsk));
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        }).onErrorResume(throwable -> {
            throw new TaskManagerException(HttpStatus.BAD_REQUEST, throwable.getMessage());
        });
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ResponseModel<Task>>> getAllTask() {
        return this.taskService.getAllTasks().flatMap(Flux::just).collectList().flatMap(allTask -> {
            ResponseModel<Task> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(allTask);
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        }).onErrorResume(throwable -> {
            throw new TaskManagerException(HttpStatus.BAD_REQUEST, throwable.getMessage());
        });
    }

    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<ResponseModel<Task>>> getTaskById(@PathVariable("taskId") Integer taskId) {
        return this.taskService.getTaskById(taskId).flatMap(task -> {
            if (Objects.isNull(task.getId()) || Objects.isNull(task.getDescription())) {
                throw new TaskManagerException(HttpStatus.NOT_FOUND, "No task is found with id equals :" + taskId);
            }

            ResponseModel<Task> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(task));
            responseModel.setErrors("");

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }
}
