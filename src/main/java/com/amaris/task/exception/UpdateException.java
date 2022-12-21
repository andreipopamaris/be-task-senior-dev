package com.amaris.task.exception;

public class UpdateException extends RuntimeException {
	private static final long serialVersionUID = -5037874612119848719L;

	public UpdateException(final String exMessage) {
		this(exMessage, null);
	}
	
	public UpdateException(final String exMessage, final Throwable throwable) {
		super(exMessage, throwable);
	}
}