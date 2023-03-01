package com.amaris.task.service;

import com.amaris.task.dto.TaskDto;
import com.amaris.task.entity.Task;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.exception.TaskManagerException;
import com.amaris.task.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TaskService {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TaskRepository taskRepository;

    public Mono<Task> updateTask(TaskDto taskDto) {
        Task task = Task.builder()
                .id(taskDto.getId())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(taskDto.getStatus())
                .build();

        return this.r2dbcEntityTemplate.update(task).flatMap(Mono::just)
                .onErrorMap(throwable -> {
                    if (throwable.getMessage().contains("Failed to update table [task]. Row with Id [" + taskDto.getId() + "] does not exist")) {
                        TaskManagerErrorCode errorCode = TaskManagerErrorCode.FAILED_TO_UPDATE_TASK;
                        throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + taskDto.getId() + " is found");
                    } else if (throwable.getMessage().contains("Referential integrity constraint violation")) {
                        TaskManagerErrorCode errorCode = TaskManagerErrorCode.REFERENTIAL_INTEGRITY_CONSTRAINT;
                        throw new TaskManagerException(errorCode.getHttpStatus(), errorCode.getErrorCode(), errorCode.getMessage() + taskDto.getAssignee() + " is found");
                    } else {
                        throw new TaskManagerException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR-CUS-002", throwable.getMessage());
                    }
                })
                .defaultIfEmpty(Task.builder().build());
    }

    public Mono<Task> assignTask(TaskDto taskDto) {
        Task tsk = Task.builder()
                .id(taskDto.getId())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(taskDto.getStatus())
                .build();
        return this.taskRepository.save(tsk).flatMap(Mono::just)
                .onErrorMap(throwable -> {
                    if (throwable.getMessage().contains("Referential integrity constraint violation")) {
                        TaskManagerErrorCode errorCode = TaskManagerErrorCode.REFERENTIAL_INTEGRITY_CONSTRAINT;
                        throw new TaskManagerException(
                                errorCode.getHttpStatus(), errorCode.getErrorCode(),
                                errorCode.getMessage() + taskDto.getAssignee() + " is found");
                    } else {
                        throw new TaskManagerException(HttpStatus.INTERNAL_SERVER_ERROR, "ERR-CUS-003", throwable.getMessage());
                    }
                }).defaultIfEmpty(Task.builder().build());
    }

    public Flux<Task> getAllTasks() {
        return this.taskRepository.findAll().flatMap(Flux::just).defaultIfEmpty(Task.builder().build());
    }

    public Mono<Task> getTaskById(Integer taskId) {
        return this.taskRepository.findById(taskId).flatMap(Mono::just).defaultIfEmpty(Task.builder().build());
    }

    public Mono<Void> deleteAllTask() {
        return this.taskRepository.deleteAll().flatMap(Mono::just);
    }
}
