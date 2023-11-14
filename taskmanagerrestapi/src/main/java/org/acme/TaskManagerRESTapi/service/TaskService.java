package org.acme.TaskManagerRESTapi.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.acme.TaskManagerRESTapi.dto.TaskDTO;
import org.acme.TaskManagerRESTapi.models.Task;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO readTask(Long taskId);
    List<Task> getAllTasks();
    TaskDTO updateTask(Long taskId, TaskDTO taskUpdate);
    TaskDTO completeTask(Long taskId);
    void writeCompletedTasksToCsv();
    String getFileContent(MultipartFile userFile);
    void downloadResource(HttpServletRequest request, HttpServletResponse response);
    void deleteTask(Long taskId);
    List<Task> deleteCompletedTasks();
    TaskDTO mapToDto(Task task);
}
