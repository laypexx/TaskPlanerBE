package org.acme.TaskManagerRESTapi.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.acme.TaskManagerRESTapi.dto.TaskDTO;
import org.acme.TaskManagerRESTapi.models.Task;
import org.acme.TaskManagerRESTapi.repository.TaskRepository;
import org.acme.TaskManagerRESTapi.service.impl.TaskServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    public void TaskService_CreateTask_ReturnTaskDto() {
        Task task = Task.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();
        TaskDTO taskDTO = TaskDTO.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();

        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        TaskDTO savedTask = taskService.createTask(taskDTO);

        Assertions.assertThat(savedTask).isNotNull();
    }

    @Test
    public void TaskService_ReadTaskById_ReturnTask() {
        Task task = Task.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.ofNullable(task));

        TaskDTO savedTask = taskService.readTask(1L);

        Assertions.assertThat(savedTask).isNotNull();
    }

    @Test
    public void TaskService_GetAllTasks_ReturnTaskList() {
        Task task = Task.builder()
                .description("hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-15")).build();
        Task task2 = Task.builder()
                .description("lernen")
                .dueDate(LocalDate.parse("2023-10-15")).build();

        List<Task> taskList = List.of(task, task2);
        when(taskRepository.findAll()).thenReturn(taskList);

        List<Task> savedTaskList = taskService.getAllTasks();

        Assertions.assertThat(savedTaskList).isNotNull();
    }

    @Test
    public void TaskService_UpdateTask_ReturnTaskDto() {
        Task task = Task.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();
        TaskDTO taskDTO = TaskDTO.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.ofNullable(task));
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        TaskDTO updateReturn = taskService.updateTask(1L, taskDTO);

        Assertions.assertThat(updateReturn).isNotNull();
    }

    @Test
    public void TaskService_CompleteTask_ReturnTaskDto() {
        Task task = Task.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.ofNullable(task));
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        TaskDTO completeReturn = taskService.completeTask(1L);

        Assertions.assertThat(completeReturn).isNotNull();
    }

    @Test
    public void TaskService_WriteCompletedTasksToCsv_ReturnVoid() {
        Task completedTask1 = Task.builder()
                .id(1L)
                .description("hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-15"))
                .isCompleted(true).build();
        Task completedTask2 = Task.builder()
                .id(2L)
                .description("lernen")
                .dueDate(LocalDate.parse("2023-10-15"))
                .isCompleted(true).build();

        List<Task> completedTasks = List.of(completedTask1, completedTask2);

        when(taskRepository.findByIsCompleted(true)).thenReturn(completedTasks);

        assertAll(() -> taskService.writeCompletedTasksToCsv());
    }

    @Test
    public void TaskService_GetFileContent_ReturnContentAsString() throws IOException {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        String expectedContent = "Test content";
        when(mockFile.getBytes()).thenReturn(expectedContent.getBytes(StandardCharsets.UTF_8));

        String actualContent = taskService.getFileContent(mockFile);

        assertEquals(expectedContent, actualContent);
    }

    @Test
    public void TaskService_DownloadResource_ReturnVoid() {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        String fileName = "CompletedTasks.csv";
        String fileDir = "./csvfiles/";
        Path file = Paths.get(fileDir + fileName);

        taskService.downloadResource(request, response);

        assertTrue(Files.exists(file));
        assertEquals("text/plain", response.getContentType());
        assertEquals("attachment; filename=" + fileName, response.getHeader("Content-Disposition"));
    }

    @Test
    public void TaskService_DeleteCompletedTasks_ReturnCompletedTasks() {
        Task completedTask1 = Task.builder()
                .id(1L)
                .description("hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-15"))
                .isCompleted(true).build();
        Task completedTask2 = Task.builder()
                .id(2L)
                .description("lernen")
                .dueDate(LocalDate.parse("2023-10-15"))
                .isCompleted(true).build();

        List<Task> taskList = List.of(completedTask1, completedTask2);
        when(taskRepository.findByIsCompleted(true)).thenReturn(taskList);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(completedTask1));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(completedTask2));

        List<Task> completedTasks = taskService.deleteCompletedTasks();

        Assertions.assertThat(completedTasks).isNotNull();
        assertEquals(taskList, completedTasks);
    }

    @Test
    public void TaskService_DeleteTaskById_ReturnVoid() {
        Task task = Task.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.ofNullable(task));

        assertAll(() -> taskService.deleteTask(1L));
    }
}
