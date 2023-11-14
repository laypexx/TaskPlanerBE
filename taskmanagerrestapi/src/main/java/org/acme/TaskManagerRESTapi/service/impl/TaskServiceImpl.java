package org.acme.TaskManagerRESTapi.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.acme.TaskManagerRESTapi.dto.TaskDTO;
import org.acme.TaskManagerRESTapi.models.Task;
import org.acme.TaskManagerRESTapi.repository.TaskRepository;
import org.acme.TaskManagerRESTapi.service.TaskService;
import org.acme.TaskManagerRESTapi.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    //create a task
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());

        Task newTask = taskRepository.save(task);

        TaskDTO taskResponse = new TaskDTO();
        taskResponse.setDescription(newTask.getDescription());
        taskResponse.setDueDate(newTask.getDueDate());
        return taskResponse;
    }

    //get one task
    public TaskDTO readTask(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task could not be found"));
        return mapToDto(task);
    }

    //get all tasks
    public List<Task> getAllTasks() {
        return (List<Task>) taskRepository.findAll();
    }

    //update a task
    public TaskDTO updateTask(@PathVariable Long taskId, @RequestBody TaskDTO taskUpdate) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task could not be updated"));

        task.setDescription(taskUpdate.getDescription());
        task.setDueDate(taskUpdate.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    //mark a task as completed
    public TaskDTO completeTask(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task could not be marked as completed"));

        task.setIsCompleted(true);

        Task completedTask = taskRepository.save(task);

        return mapToDto(completedTask);
    }

    //write all completed Tasks to csv file
    public void writeCompletedTasksToCsv() {
        CsvFileWriterServiceImpl csvFileWriterService = new CsvFileWriterServiceImpl();
        List<Task> completedTasks = taskRepository.findByIsCompleted(true);

        csvFileWriterService.writeTaskToCsv(completedTasks, "./csvfiles/CompletedTasks.csv");
    }

    //gets File Content of Mulitpartfile in a String
    public String getFileContent(MultipartFile userfile) {
        String content;
        try {
            content = new String(userfile.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    //gives downloadfile to api for download
    public void downloadResource(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "CompletedTasks.csv";
        String fileDir = "./csvfiles/";
        Path file = Paths.get(fileDir+fileName);
        if (Files.exists(file)) {
            response.setContentType("text/plain");
            response.setHeader("Content-Type", "text/plain");
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Datei nicht gefunden.");
        }
    }

    // delete a task
    public void deleteTask(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task could not be deleted"));
        taskRepository.delete(task);
    }

    // deletes all completed tasks from db
    public List<Task> deleteCompletedTasks() {
        List<Task> completedTasks = taskRepository.findByIsCompleted(true);
        List<Long> completedTaskIds = completedTasks.stream()
                .map(Task::getId)
                .toList();
        completedTaskIds.forEach(this::deleteTask);
        return completedTasks;
    }

    public TaskDTO mapToDto(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        return taskDTO;
    }
}
