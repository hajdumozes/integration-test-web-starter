package com.poc.integrationtestwebstarter.service;

import com.poc.integrationtestwebstarter.entity.Entity;
import com.poc.integrationtestwebstarter.repository.EntityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityService {
    EntityRepository repository;

    public List<Entity> findAll() {
        return repository.findAll();
    }

    public Optional<Entity> findById(Integer id) {
        return repository.findById(id);
    }

    public void persist(Entity entity) {
        repository.save(entity);
    }

    public void update(Integer id, Entity entity) {
        repository.findById(id)
                .ifPresent(existing -> repository.save(entity));
    }

    public void delete(Integer id) {
        repository.findById(id).ifPresent(repository::delete);
    }
}
