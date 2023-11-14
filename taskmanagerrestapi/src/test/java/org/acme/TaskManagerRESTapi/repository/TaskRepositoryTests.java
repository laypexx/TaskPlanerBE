package org.acme.TaskManagerRESTapi.repository;

import org.acme.TaskManagerRESTapi.models.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskRepositoryTests {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void TaskRepository_SaveAll_ReturnSavedTask() {

        //Arrange
        Task task = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        //Act
        Task savedTask = taskRepository.save(task);

        //Assert
        Assertions.assertThat(savedTask).isNotNull();
        Assertions.assertThat(savedTask.getId()).isGreaterThan(0);
    }

    @Test
    public void TaskRepository_GetAll_ReturnMoreThenOneTask() {
        Task task = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();
        Task task2 = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        taskRepository.save(task);
        taskRepository.save(task2);

        List<Task> taskList = taskRepository.findAll();

        Assertions.assertThat(taskList).isNotNull();
        Assertions.assertThat(taskList.size()).isEqualTo(2);
    }

    @Test
    public void TaskRepository_FindById_ReturnTask() {
        Task task = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        taskRepository.save(task);

        Task taskResult = taskRepository.findById(task.getId()).get();

        Assertions.assertThat(taskResult).isNotNull();
    }

    @Test
    public void TaskRepository_FindByDueDate_ReturnTaskNotNull() {
        Task task = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        taskRepository.save(task);

        Task taskSave = taskRepository.findByDueDate(task.getDueDate()).get();

        Assertions.assertThat(taskSave).isNotNull();
    }

    @Test
    public void TaskRepository_UpdateTask_ReturnTaskNotNull() {
        Task task = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        taskRepository.save(task);

        Task taskSave = taskRepository.findById(task.getId()).get();
        taskSave.setDueDate(LocalDate.parse("2023-10-10"));
        taskSave.setDescription("hausis machen");

        Task updatedTask = taskRepository.save(taskSave);

        Assertions.assertThat(updatedTask.getId()).isEqualTo(task.getId());
        Assertions.assertThat(updatedTask.getDescription()).isNotNull();
        Assertions.assertThat(updatedTask.getDueDate()).isNotNull();
    }

    @Test
    public void TaskRepository_DeleteById_ReturnTaskIsEmpty() {
        Task task = Task.builder()
                .description("hausaufgaben machen 2")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        taskRepository.save(task);

        taskRepository.deleteById(task.getId());
        Optional<Task> taskReturn = taskRepository.findById(task.getId());

        Assertions.assertThat(taskReturn).isEmpty();
    }
}
