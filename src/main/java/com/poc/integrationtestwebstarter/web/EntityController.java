package com.poc.integrationtestwebstarter.web;

import com.poc.integrationtestwebstarter.dto.EntityDto;
import com.poc.integrationtestwebstarter.entity.Entity;
import com.poc.integrationtestwebstarter.mapper.EntityMapper;
import com.poc.integrationtestwebstarter.service.EntityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/entities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityController {
    EntityService service;
    EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<List<EntityDto>> findEntities() {
        List<Entity> entities = service.findAll();
        return ResponseEntity.ok(entityMapper.toDtoList(entities));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityDto> findEntity(@PathVariable Integer id) {
        return service.findById(id)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> persistEntity(@RequestBody EntityDto dto) {
        service.persist(entityMapper.toEntity(dto));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody EntityDto dto) {
        service.update(id, entityMapper.toEntity(dto));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
