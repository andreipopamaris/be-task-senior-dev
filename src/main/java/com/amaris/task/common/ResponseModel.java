package com.amaris.task.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseModel<T> implements Serializable {
    private ResponseStatus status;
    private List<T> payload;
    private String errors;
}
