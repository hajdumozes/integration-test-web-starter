package com.poc.integrationtestwebstarter.repository;

import com.poc.integrationtestwebstarter.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Integer> {
}
