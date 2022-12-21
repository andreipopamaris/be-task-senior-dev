package com.amaris.task.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaris.task.exception.TaskActionException;
import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;
import com.amaris.task.service.CrudTaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskActionServiceImpl implements TaskActionService {
	private final CrudTaskService crudTaskService;

	public void assignTask(final Task task, final Employee employee) {
		log.info("assignTask START - args=[task={}, employee={}]", task, employee);
		if ( !TransitionStatusTaskChecker.canTransiteToAssignable(task) ) {
			final String errorMessage = String.format("Task with id: %s cannot be assignable because is already assigned. Try with reassigment action of task", task.getId());
			throw new TaskActionException(errorMessage);
		}

		task.setAssignee(employee);
		task.setStatus(Task.Status.ASSIGNED);
		this.crudTaskService.update(task.getId(), task);
	}

	public void reassignTask(final Task task, final Employee employee) {
		log.info("reassignTask START - args=[task={}, employee={}]", task, employee);
		final String errorMessage = String.format("Task with id: %s cannot be reassignable because is unassigned. Try to assigned task before reassigned it.", task.getId());
		if (!TransitionStatusTaskChecker.canTransiteToUnassignable(task)) {
			throw new TaskActionException(errorMessage);
		}

		task.setAssignee(employee);
		task.setStatus(Task.Status.REASSIGNED);
		this.crudTaskService.update(task.getId(), task);
	}

	public void unassignTask(final Task task) {
		log.info("unassignTask START - args=[task={}]", task);
		final String errorMessage = String.format("Task with id: %s cannot be unassignable because is already unassigned.", task.getId());
		if ( !TransitionStatusTaskChecker.canTransiteToUnassignable(task) ) {
			throw new TaskActionException(errorMessage);
		}

		task.setAssignee(null);
		task.setStatus(Task.Status.UNASSIGNED);
		this.crudTaskService.update(task.getId(), task);
	}
}