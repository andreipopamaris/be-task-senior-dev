package com.amaris.task.exception;

public class TaskActionException extends RuntimeException {
	private static final long serialVersionUID = -6463055513793477974L;

	public TaskActionException(final String exMessage) {
		this(exMessage, null);
	}
	
	public TaskActionException(final String exMessage, final Throwable throwable) {
		super(exMessage, throwable);
	}
}
