package com.amaris.task.handler;

import com.amaris.task.model.TaskAction;

public final class TaskActionHandlerFactory {
	private TaskActionHandlerFactory() { }

	public static TaskActionHandler of(final TaskAction taskAction) {
		switch (taskAction) {
			case ASSIGNMENT:
				return new AssignmentTaskHandler();
			case REASSIGNMENT:
				return new ReassignimentTaskHandler();
			case UNASSIGNMENT:
				return new UnassignmentTaskHandler();
			default:
				throw new UnsupportedOperationException("Action not supported!");
		}
	}
}