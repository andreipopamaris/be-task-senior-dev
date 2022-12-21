package com.amaris.task.exception;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1814647731106261008L;

	public ResourceNotFoundException(final String exMessage) {
		this(exMessage, null);
	}
	
	public ResourceNotFoundException(final String exMessage, final Throwable throwable) {
		super(exMessage, throwable);
	}
}