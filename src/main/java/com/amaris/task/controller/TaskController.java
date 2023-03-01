package com.amaris.task.controller;

import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.common.response.ResponseStatus;
import com.amaris.task.dto.TaskDto;
import com.amaris.task.entity.Status;
import com.amaris.task.entity.Task;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/assign")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> assignTask(@Valid @RequestBody TaskDto taskDto) {

        return this.taskService.assignTask(TaskDto.builder()
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(Status.ASSIGNED)
                .build()).flatMap(tsk -> {

            ResponseModel<TaskDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(TaskDto.builder()
                    .id(tsk.getId())
                    .description(tsk.getDescription())
                    .dueDate(tsk.getDueDate())
                    .assignee(tsk.getAssignee())
                    .status(tsk.getStatus())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @PutMapping("/unassign")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> unassign(@Valid @RequestBody TaskDto taskDto) {
        return manageTask(taskDto, Status.UNASSIGNED).flatMap(Mono::just);
    }

    @PutMapping("/reassign")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> reassign(@Valid @RequestBody TaskDto taskDto) {
        return manageTask(taskDto, Status.ASSIGNED).flatMap(Mono::just);
    }

    @PutMapping("/edit")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> updateDueDate(@Valid @RequestBody TaskDto taskDto) {
        return manageTask(taskDto, Status.ASSIGNED).flatMap(Mono::just);
    }

    public Mono<ResponseEntity<ResponseModel<TaskDto>>> manageTask(TaskDto taskDto, Status status) {

        if (Objects.isNull(taskDto.getId())) {
            TaskManagerErrorCode errorCode = TaskManagerErrorCode.TASK_ID_IS_MANDATORY;
            throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage());
        }

        return this.taskService.updateTask(TaskDto.builder()
                .id(taskDto.getId())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(status)
                .build()).flatMap(tsk -> {

            ResponseModel<TaskDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);

            responseModel.setPayload(Collections.singletonList(TaskDto.builder()
                    .id(tsk.getId())
                    .description(tsk.getDescription())
                    .dueDate(tsk.getDueDate())
                    .assignee(tsk.getAssignee())
                    .status(tsk.getStatus())
                    .build()));

            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> getAllTask() {
        return this.taskService.getAllTasks().flatMap(Flux::just).collectList().flatMap(allTask -> {

            if (Objects.isNull(allTask.get(0).getId())) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.NO_TASK_FOUND;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage());
            }

            List<TaskDto> taskDtoList = new ArrayList<>();
            for (Task tsk : allTask) {
                TaskDto taskDto = new TaskDto();
                taskDto.setId(tsk.getId());
                taskDto.setDescription(tsk.getDescription());
                taskDto.setDueDate(tsk.getDueDate());
                taskDto.setAssignee(tsk.getAssignee());
                taskDto.setStatus(tsk.getStatus());
                taskDtoList.add(taskDto);
            }

            ResponseModel<TaskDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(taskDtoList);
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }

    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> getTaskById(@PathVariable("taskId") Integer taskId) {

        if (Objects.isNull(taskId)) {
            TaskManagerErrorCode errorCode = TaskManagerErrorCode.TASK_ID_IS_MANDATORY;
            throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage());
        }

        return this.taskService.getTaskById(taskId).flatMap(taskDto -> {
            if (Objects.isNull(taskDto.getId()) || Objects.isNull(taskDto.getDescription())) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.NO_TASK_IS_FOUND_BY_ID;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + taskId);
            }

            ResponseModel<TaskDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(TaskDto.builder()
                    .id(taskDto.getId())
                    .description(taskDto.getDescription())
                    .dueDate(taskDto.getDueDate())
                    .assignee(taskDto.getAssignee())
                    .status(taskDto.getStatus())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        });
    }
}
