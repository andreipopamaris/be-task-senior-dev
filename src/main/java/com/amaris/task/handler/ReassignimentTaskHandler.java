package com.amaris.task.handler;

import javax.transaction.Transactional;

import com.amaris.task.model.Employee;
import com.amaris.task.model.Task;

public final class ReassignimentTaskHandler extends TaskActionHandler {
	@Override
	@Transactional
	public void handleAction(final Task task, final Employee employee) {
		this.taskActionService.reassignTask(task, employee);
	}
}