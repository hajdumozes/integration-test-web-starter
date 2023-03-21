package com.poc.integrationtestwebstarter.web;

import com.poc.integrationtestwebstarter.entity.Entity;
import com.poc.integrationtestwebstarter.repository.EntityRepository;
import com.poc.integrationtestwebstarter.web.config.DatabaseConfig;
import com.poc.integrationtestwebstarter.web.config.WebConfig;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DatabaseConfig.class, WebConfig.class})
@WebAppConfiguration
@FieldDefaults(level = AccessLevel.PRIVATE)
class EntityControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    @Autowired
    EntityRepository repository;

    final ObjectMapper objectMapper = new ObjectMapper();

    final ObjectWriter objectWriter;

    EntityControllerTest() {
        this.objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        ;
    }

    @BeforeEach
    void init() {
        repository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldReturnEntities_onFindingAll() throws Exception {
        // given
        repository.save(new Entity(1, "test entity"));

        // when-then
        mockMvc.perform(get("/entities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnEntity_onFindingById_givenExistingEntityWithGivenId() throws Exception {
        // given
        Entity storedEntity = new Entity(1, "test entity");
        repository.save(storedEntity);

        // when
        String output = mockMvc.perform(get("/entities/" + storedEntity.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        Entity result = objectMapper.readValue(output, Entity.class);
        assertEquals(storedEntity, result);
    }

    @Test
    void shouldReturnNotFound_onFindingById_givenNoEntityWithGivenId() throws Exception {
        // given
        Entity storedEntity = new Entity(1, "test entity");
        repository.save(storedEntity);

        // when-then
        mockMvc.perform(get("/entities/" + 2))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldPersistEntity_onPersisting() throws Exception {
        // given
        Entity entityToStore = new Entity(1, "test entity");

        // when
        mockMvc.perform(post("/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(entityToStore)))
                .andExpect(status().isOk());

        // then
        Entity storedEntity = repository.findById(entityToStore.getId()).orElseThrow();
        assertEquals(storedEntity, entityToStore);
    }

    @Test
    void shouldUpdate_onUpdating() throws Exception {
        // given
        Entity storedEntity = new Entity(1, "test entity");
        repository.save(storedEntity);
        Entity update = new Entity(1, "new description");

        // when
        mockMvc.perform(put("/entities/" + storedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(update)))
                .andExpect(status().isOk());

        // then
        Entity updatedEntity = repository.findById(storedEntity.getId()).orElseThrow();
        assertEquals(updatedEntity, update);
    }

    @Test
    void shouldDeleteEntity_onDeleting() throws Exception {
        // given
        Entity storedEntity = new Entity(1, "test entity");
        repository.save(storedEntity);

        // when
        mockMvc.perform(delete("/entities/" + storedEntity.getId()))
                .andExpect(status().isOk());

        // then
        assertTrue(repository.findAll().isEmpty());
    }

}