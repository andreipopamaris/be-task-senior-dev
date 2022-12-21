package com.amaris.task.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaris.task.entity.TaskEntity;
import com.amaris.task.exception.SaveException;
import com.amaris.task.exception.UpdateException;
import com.amaris.task.model.Task;
import com.amaris.task.repository.TaskRepository;
import com.amaris.task.service.CrudTaskService;

import lombok.extern.slf4j.Slf4j;

@Service(value = "crudTaskService")
@Slf4j
public class CrudTaskServiceImpl implements CrudTaskService {
	protected final TaskRepository taskRepository;
	protected final ModelMapper modelMapper;

	public CrudTaskServiceImpl(TaskRepository taskRepository, ModelMapper modelMapper) {
		this.taskRepository = taskRepository;
		this.modelMapper = modelMapper;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Task> getAll() {
		log.info("getAll START");
		return this.taskRepository.findAllAsStream()
				.map(taskEntity -> this.modelMapper.map(taskEntity, Task.class))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<Task> getById(final Long id) {
		log.info("getById START - args=[id={}]", id);
		return this.taskRepository
			.findById(id)
			.map(taskEntity -> this.modelMapper.map(taskEntity, Task.class));
	}

	@Transactional
	@Override
	public Long save(final Task task) {
		log.info("save START - args=[task={}]", task);
		final Long taskId = task.getId();
		if( !Objects.isNull(taskId) && this.taskRepository.existsById(taskId) ) {
			throw new SaveException(
				String.format("Error to Save Task. Task with id: %s already exists", taskId)
			);
		}
		
		final TaskEntity taskEntityToSave = this.modelMapper.map(task, TaskEntity.class);
		TaskEntity a = this.taskRepository.save(taskEntityToSave);
		return a.getId();
	}

	@Transactional
	@Override
	public void update(final Long id, final Task task) {
		log.info("update START - args=[task={}]", task);
		this.taskRepository
			.findById(id)
			.map(taskEntity -> {
				taskEntity = this.modelMapper.map(task, TaskEntity.class);
				taskEntity.setId(id);
				TaskEntity taskUpdate = this.taskRepository.save(taskEntity);
				return taskUpdate;
			})
			.orElseThrow(
				() -> new UpdateException(String.format("Error to Update Task. Task with id: %s not exists", id))
			);
	}

	@Transactional
	@Override
	public void deleteById(final Long id) {
		log.info("deleteById START - args=[id={}]", id);
		this.taskRepository
			.findById(id)
			.ifPresent(taskRepository::delete);
	}
}