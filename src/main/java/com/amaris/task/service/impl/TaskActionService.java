package com.amaris.task.service.impl;

import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;

public interface TaskActionService {
	void assignTask(final Task task, final Employee employee);
	void reassignTask(final Task task, final Employee employee);
	void unassignTask(final Task task);
}