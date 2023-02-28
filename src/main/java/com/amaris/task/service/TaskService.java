package com.amaris.task.service;

import com.amaris.task.entity.Task;
import com.amaris.task.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TaskService {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TaskRepository taskRepository;

    public Mono<Task> updateTask(Task task) {
        return this.r2dbcEntityTemplate.update(task).flatMap(Mono::just).defaultIfEmpty(Task.builder().build());
    }

    public Mono<Task> manageTask(Task task) {
        return this.taskRepository.save(task).flatMap(Mono::just).defaultIfEmpty(Task.builder().build());
    }

    public Flux<Task> getAllTasks() {
        return this.taskRepository.findAll().flatMap(Flux::just).defaultIfEmpty(Task.builder().build());
    }

    public Mono<Void> deleteAllTask() {
        return this.taskRepository.deleteAll().flatMap(Mono::just);
    }

    public Mono<Task> getTaskById(Integer taskId) {
        return this.taskRepository.findById(taskId).flatMap(Mono::just).defaultIfEmpty(Task.builder().build());
    }
}
