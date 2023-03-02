package com.amaris.task.controller;

import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.dto.TaskDto;
import com.amaris.task.entity.Status;
import com.amaris.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

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
                .build()).flatMap(Mono::just);
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

        return this.taskService.updateTask(TaskDto.builder()
                .id(taskDto.getId())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .assignee(taskDto.getAssignee())
                .status(status)
                .build()).flatMap(Mono::just);
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> getAllTask() {
        return this.taskService.getAllTasks().flatMap(Mono::just);
    }

    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<ResponseModel<TaskDto>>> getTaskById(@PathVariable("taskId") Integer taskId) {
        return this.taskService.getTaskById(taskId).flatMap(Mono::just);
    }
}
