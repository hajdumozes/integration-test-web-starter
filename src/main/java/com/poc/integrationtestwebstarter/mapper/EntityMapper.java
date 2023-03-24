package com.poc.integrationtestwebstarter.mapper;

import com.poc.integrationtestwebstarter.dto.EntityDto;
import com.poc.integrationtestwebstarter.entity.Entity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    EntityDto toDto(Entity entity);

    Entity toEntity(EntityDto dto);

    List<EntityDto> toDtoList(List<Entity> source);

    List<Entity> toEntityList(List<EntityDto> source);
}
