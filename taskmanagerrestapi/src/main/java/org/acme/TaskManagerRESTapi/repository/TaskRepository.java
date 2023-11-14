package org.acme.TaskManagerRESTapi.repository;

import org.acme.TaskManagerRESTapi.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//db repository
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByDueDate(LocalDate date);
    List<Task> findByIsCompleted(boolean completed);
}