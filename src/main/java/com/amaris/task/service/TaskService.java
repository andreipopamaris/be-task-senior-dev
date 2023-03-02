package com.amaris.task.service;

import com.amaris.task.common.response.ErrorModel;
import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.common.response.ResponseStatus;
import com.amaris.task.dto.TaskDto;
import com.amaris.task.entity.Task;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TaskService {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TaskRepository taskRepository;

    public Mono<ResponseEntity<ResponseModel<TaskDto>>> updateTask(TaskDto taskDto) {
        if (Objects.isNull(taskDto.getId())) {
            TaskManagerErrorCode errorCode = TaskManagerErrorCode.TASK_ID_IS_MANDATORY;
            throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage());
        }

        Task task = Task.builder()
                .id(taskDto.getId())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(taskDto.getStatus())
                .build();

        return this.r2dbcEntityTemplate.update(task).flatMap(tsk -> {
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
        }).onErrorMap(throwable -> {
            if (throwable.getMessage().contains("Failed to update table [task]. Row with Id [" + taskDto.getId() + "] does not exist")) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.FAILED_TO_UPDATE_TASK;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + taskDto.getId() + " is found");
            } else if (throwable.getMessage().contains("Referential integrity constraint violation")) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.REFERENTIAL_INTEGRITY_CONSTRAINT;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + taskDto.getAssignee() + " exists");
            } else {
                throw new TaskManagerException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR-CUS-002", throwable.getMessage());
            }
        });
    }

    public Mono<ResponseEntity<ResponseModel<TaskDto>>> assignTask(TaskDto taskDto) {
        Task tsk = Task.builder()
                .id(taskDto.getId())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(taskDto.getStatus())
                .build();

        return this.taskRepository.save(tsk).flatMap(tskDto -> {

            ResponseModel<TaskDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(TaskDto.builder()
                    .id(tskDto.getId())
                    .description(tskDto.getDescription())
                    .dueDate(tskDto.getDueDate())
                    .assignee(tskDto.getAssignee())
                    .status(tskDto.getStatus())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        }).onErrorMap(throwable -> {

            if (throwable.getMessage().contains("Referential integrity constraint violation")) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.REFERENTIAL_INTEGRITY_CONSTRAINT;
                throw new TaskManagerException(
                        errorCode.getHttpStatus(),
                        errorCode.getErrorCode(),
                        errorCode.getMessage() + taskDto.getAssignee() + " exist"
                );
            } else {
                throw new TaskManagerException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR-CUS-003", throwable.getMessage());
            }
        });
    }

    public Mono<ResponseEntity<ResponseModel<TaskDto>>> getAllTasks() {
        return this.taskRepository.findAll().flatMap(Flux::just).collectList().flatMap(allTask -> {
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

    public Mono<ResponseEntity<ResponseModel<TaskDto>>> getTaskById(Integer taskId) {
        if (Objects.isNull(taskId)) {
            TaskManagerErrorCode errorCode = TaskManagerErrorCode.TASK_ID_IS_MANDATORY;
            throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage());
        }

        return this.taskRepository.findById(taskId).flatMap(task -> {
            if (Objects.isNull(task.getId()) || Objects.isNull(task.getDescription()) || task.equals(Task.builder().build())) {
                TaskManagerErrorCode errorCode = TaskManagerErrorCode.NO_TASK_IS_FOUND_BY_ID;
                throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + taskId);
            }

            ResponseModel<TaskDto> responseModel = new ResponseModel<>();
            responseModel.setStatus(ResponseStatus.OK);
            responseModel.setPayload(Collections.singletonList(TaskDto.builder()
                    .id(task.getId())
                    .description(task.getDescription())
                    .dueDate(task.getDueDate())
                    .assignee(task.getAssignee())
                    .status(task.getStatus())
                    .build()));
            responseModel.setErrors(null);

            return Mono.just(ResponseEntity.ok(responseModel));
        }).defaultIfEmpty(ResponseEntity.ok(
                new ResponseModel<TaskDto>(
                        ResponseStatus.KO,
                        null,
                        new ErrorModel(TaskManagerErrorCode.NO_TASK_IS_FOUND_BY_ID.getErrorCode(),
                                TaskManagerErrorCode.NO_TASK_IS_FOUND_BY_ID.getMessage() + taskId)
                )
        ));
    }

    public Mono<Void> deleteAllTask() {
        return this.taskRepository.deleteAll().flatMap(Mono::just);
    }
}
