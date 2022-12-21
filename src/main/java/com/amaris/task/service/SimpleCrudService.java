package com.amaris.task.service;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

// Should be move into common lib
public interface SimpleCrudService<MODEL, ID> {
	public List<MODEL> getAll();
	public Optional<MODEL> getById(@NotNull final ID id);
	public ID save(@NotNull final MODEL entity);
	public void update(@NotNull final ID id, @NotNull final MODEL entity);
	public void deleteById(@NotNull final ID id);
}