package com.amaris.task.service.impl;

import com.amaris.task.model.Task;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransitionStatusTaskChecker {
	
	public static boolean canTransiteToAssignable(final Task task) {
		final boolean result = task.getStatus() == Task.Status.UNASSIGNED 
				? Boolean.TRUE 
				: Boolean.FALSE;
		
		log.info("task with id:{} can be assigned? {}", task.getId(), result);
		return result;
	}

	public static boolean canTransiteToUnassignable(final Task task) {
		final boolean result = task.getStatus() == Task.Status.UNASSIGNED 
				? Boolean.FALSE 
				: Boolean.TRUE;
		
		log.info("task with id:{} can be unassigned? {}", task.getId(), result);
		return result;
	}
	
	public static boolean canTransiteToReassignable(final Task task) {
		final boolean result = (task.getStatus() == Task.Status.ASSIGNED || task.getStatus() == Task.Status.REASSIGNED) 
				? Boolean.TRUE
				: Boolean.FALSE;
		
		log.info("task with id:{} can ibes reassigned? {}", task.getId(), result);
		return result;
	}
}
