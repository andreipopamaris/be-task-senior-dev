package com.amaris.task.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.amaris.task.entity.EmployeeEntity;

@Repository
public interface EmployeeRepository  extends JpaRepository<EmployeeEntity, Long> {
	@Query(value = "SELECT e FROM EmployeeEntity e")
	Stream<EmployeeEntity> findAllAsStream();
}