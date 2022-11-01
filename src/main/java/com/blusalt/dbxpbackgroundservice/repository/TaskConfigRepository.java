package com.blusalt.dbxpbackgroundservice.repository;

import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskConfigRepository extends JpaRepository<BluTaskConfig,Long> {

    List<BluTaskConfig> findAllByStatusNotContainingIgnoreCase(String status, Pageable pageable);

    List<BluTaskConfig> findAllByStatus(String status);
    Optional<BluTaskConfig> findBluTaskConfigByIdAndStatus( Long id, String status);
}
