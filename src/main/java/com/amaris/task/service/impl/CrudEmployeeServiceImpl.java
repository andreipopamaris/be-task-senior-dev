package com.amaris.task.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaris.task.entity.EmployeeEntity;
import com.amaris.task.exception.SaveException;
import com.amaris.task.exception.UpdateException;
import com.amaris.task.model.Employee;
import com.amaris.task.repository.EmployeeRepository;
import com.amaris.task.service.CrudEmployeeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CrudEmployeeServiceImpl implements CrudEmployeeService {
	protected final EmployeeRepository employeeRepository;
	protected final ModelMapper modelMapper;
	
	public CrudEmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
		this.employeeRepository = employeeRepository;
		this.modelMapper = modelMapper;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Employee> getAll() {
		log.info("getAll START");
		return this.employeeRepository
			.findAllAsStream()
			.map( employeeEntity -> this.modelMapper.map(employeeEntity, Employee.class) )
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Employee> getById(final Long id) {
		log.info("getById START - args=[id={}]", id);
		return this.employeeRepository
			.findById(id)
			.map( employeeEntity -> this.modelMapper.map(employeeEntity, Employee.class) );
	}

	@Transactional
	@Override
	public Long save(final Employee employee) {
		log.info("save START - args=[employee={}]", employee);
		final Long employeeId = Objects.requireNonNull(employee.getId());
		if( this.employeeRepository.existsById(employeeId) ) {
			throw new SaveException(
				String.format("Error to Save Employee. Employee with id: %s already exists", employeeId)
			);
		}

		final EmployeeEntity employeeEntityToSave = this.modelMapper.map(employee, EmployeeEntity.class);
		return this.employeeRepository
				.save(employeeEntityToSave)
				.getId();
	}

	@Transactional
	@Override
	public void update(final Long id, final Employee employee) {
		log.info("update START - args=[employee={}]", employee);
		this.employeeRepository
			.findById(id)
			.map( employeeEntity -> {
				employeeEntity = this.modelMapper.map(employee, EmployeeEntity.class);
				employeeEntity.setId(id);
				return this.employeeRepository.save(employeeEntity);
			})
			.orElseThrow(
				() -> new UpdateException(String.format("Error to Update Employee. Employee with id: %s not exists", id))
			);
	}

	@Transactional
	@Override
	public void deleteById(final Long id) {
		log.info("deleteById START - args=[id={}]", id);
		this.employeeRepository
			.findById(id)
			.ifPresent(employeeRepository::delete);
	}
}