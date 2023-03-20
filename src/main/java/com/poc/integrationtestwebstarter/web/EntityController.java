package com.poc.integrationtestwebstarter.web;

import com.poc.integrationtestwebstarter.entity.Entity;
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

    @GetMapping
    public ResponseEntity<List<Entity>> findEntities() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Entity> findEntity(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> persistEntity(@RequestBody Entity entity) {
        service.persist(entity);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Entity entity) {
        service.update(id, entity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
