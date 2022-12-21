package com.amaris.task.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseError {
	public String path;
	public String timestamp;
	public String errorMessage;
	public Throwable cause;
}