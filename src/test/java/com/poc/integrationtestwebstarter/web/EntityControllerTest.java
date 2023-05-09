package com.poc.integrationtestwebstarter.web;

import com.poc.integrationtestwebstarter.dto.EntityDto;
import com.poc.integrationtestwebstarter.entity.Entity;
import com.poc.integrationtestwebstarter.mapper.EntityMapper;
import com.poc.integrationtestwebstarter.repository.EntityRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
class EntityControllerTest extends TestContainerTest {
    public static final String BASE_PATH = "/entities";

    @Autowired
    EntityRepository repository;

    @Autowired
    EntityMapper mapper;

    final ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

    @BeforeEach
    void init() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnEntities_onFindingAll() throws Exception {
        // given
        repository.save(Entity.builder().id(10).description("test entity").build());

        // when-then
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnEntity_onFindingById_givenExistingEntityWithGivenId() throws Exception {
        // given
        Entity storedEntity = Entity.builder().description("test entity").build();
        int persistedId = repository.save(storedEntity).getId();
        storedEntity.setId(persistedId);

        // when
        String output = mockMvc.perform(get(String.format("%s/%s", BASE_PATH, persistedId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        EntityDto result = objectMapper.readValue(output, EntityDto.class);
        assertEquals(mapper.toDto(storedEntity), result);
    }

    @Test
    void shouldReturnNotFound_onFindingById_givenNoEntityWithGivenId() throws Exception {
        // given
        Entity storedEntity = Entity.builder().id(10).description("test entity").build();
        repository.save(storedEntity);

        // when-then
        mockMvc.perform(get(String.format("%s/%s", BASE_PATH, 2)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldPersistEntity_onPersisting() throws Exception {
        // given
        EntityDto entityToStore = EntityDto.builder().description("test entity").build();

        // when
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(entityToStore)))
                .andExpect(status().isOk());

        // then
        Entity storedEntity = repository.findAll().get(0);
        assertEquals(storedEntity.getDescription(), entityToStore.getDescription());
    }

    @Test
    void shouldUpdate_onUpdating() throws Exception {
        // given
        Entity storedEntity = Entity.builder().description("test entity").build();
        int persistedId = repository.save(storedEntity).getId();
        EntityDto update = EntityDto.builder().id(persistedId).description("new description").build();

        // when
        mockMvc.perform(put(String.format("%s/%s", BASE_PATH, persistedId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(update)))
                .andExpect(status().isOk());

        // then
        Entity updatedEntity = repository.findById(persistedId).orElseThrow();
        assertEquals(mapper.toDto(updatedEntity), update);
    }

    @Test
    void shouldDeleteEntity_onDeleting() throws Exception {
        // given
        Entity storedEntity = Entity.builder().description("test entity").build();
        int persistedId = repository.save(storedEntity).getId();

        // when
        mockMvc.perform(delete(String.format("%s/%s", BASE_PATH, persistedId)))
                .andExpect(status().isOk());

        // then
        assertTrue(repository.findAll().isEmpty());
    }

}