package com.amaris.task.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaris.task.entity.TaskEntity;
import com.amaris.task.exception.SaveException;
import com.amaris.task.exception.UpdateException;
import com.amaris.task.model.Task;
import com.amaris.task.repository.TaskRepository;
import com.amaris.task.service.CrudTaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service(value = "crudTaskService")
@RequiredArgsConstructor
@Slf4j
public class CrudTaskServiceImpl implements CrudTaskService {
	protected final TaskRepository taskRepository;
	@Autowired
	private ModelMapper modelMapper;
	
	@Transactional(readOnly = true)
	@Override
	public List<Task> getAll() {
		log.info("getAll START");
		return this.taskRepository
			.findAllAsStream()
			.map(taskEntity -> this.modelMapper.map(taskEntity, Task.class))
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Task> getById(final Long id) {
		log.info("getById START - args=[id={}]", id);
		Optional<Task> aa = this.taskRepository
			.findById(id)
			.map(taskEntity -> this.modelMapper.map(taskEntity, Task.class));
		return aa;
	}

	@Transactional
	@Override
	public Long save(final Task task) {
		log.info("save START - args=[task={}]", task);
		final Long taskId = Objects.requireNonNull(task.getId());
		if( this.taskRepository.existsById(taskId) ) {
			throw new SaveException(
				String.format("Error to Save Task. Task with id: %s already exists", taskId)
			);
		}
		
		final TaskEntity taskEntityToSave = this.modelMapper.map(taskId, TaskEntity.class);
		return this.taskRepository.save(taskEntityToSave).getId();
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
				return this.taskRepository.save(taskEntity);
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