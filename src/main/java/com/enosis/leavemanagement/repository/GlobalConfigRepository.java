package com.enosis.leavemanagement.repository;

import com.enosis.leavemanagement.model.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, Long> {
    public Optional<GlobalConfig> findByConfigName(String name);
}
