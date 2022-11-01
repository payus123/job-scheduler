package com.blusalt.dbxpbackgroundservice.repository;

import com.blusalt.dbxpbackgroundservice.models.BluTaskConfig;
import com.blusalt.dbxpbackgroundservice.models.TaskHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    Optional<List<TaskHistory>> findAllByTaskId(BluTaskConfig taskId, Pageable pageable);
}
