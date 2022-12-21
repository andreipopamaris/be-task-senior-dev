package com.amaris.task.exception;

public class SaveException extends RuntimeException {
	private static final long serialVersionUID = -5232562122151635958L;

	public SaveException(final String exMessage) {
		this(exMessage, null);
	}
	
	public SaveException(final String exMessage, final Throwable throwable) {
		super(exMessage, throwable);
	}
}