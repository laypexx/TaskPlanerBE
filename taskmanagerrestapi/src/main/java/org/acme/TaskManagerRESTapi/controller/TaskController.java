package org.acme.TaskManagerRESTapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.acme.TaskManagerRESTapi.dto.TaskDTO;
import org.acme.TaskManagerRESTapi.models.Task;
import org.acme.TaskManagerRESTapi.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks/")
@EnableWebMvc
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //task-creation post
    @PostMapping("createTask")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskCreate) {
        return new ResponseEntity<>(taskService.createTask(taskCreate), HttpStatus.CREATED);
    }

    //get one task
    @GetMapping("readTask/{taskId}")
    public ResponseEntity<TaskDTO> readTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.readTask(taskId));
    }

    //get all tasks
    @GetMapping("getAllTasks")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    //update a task
    @PutMapping("updateTask/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO taskUpdate) {
        TaskDTO response = taskService.updateTask(taskId, taskUpdate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //mark a task as completed
    @PutMapping("completeTask/{taskId}")
    public ResponseEntity<TaskDTO> completeTask(@PathVariable Long taskId) {
        TaskDTO response = taskService.completeTask(taskId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //writes completed tasks to csv
    @PostMapping("writeCompletedTasksToCsv")
    public ResponseEntity<String> writeCompletedTasksToCsv() {
        taskService.writeCompletedTasksToCsv();
        return new ResponseEntity<>("Completed Tasks successfully written to Csv", HttpStatus.OK);
    }

    //returns filecontent as string
    @PostMapping(value = "getFileContent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE
            ,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getFileContent(@RequestParam("file") MultipartFile userFile) {
        return new ResponseEntity<>(taskService.getFileContent(userFile), HttpStatus.OK);
    }

    //gives file to download to api
    @GetMapping("downloadCompletedTasks")
    public ResponseEntity<String> downloadResource(HttpServletRequest request, HttpServletResponse response) {
        taskService.downloadResource(request, response);
        return new ResponseEntity<>("File downloaded", HttpStatus.OK);
    }

    //delete a task
    @DeleteMapping("deleteTask/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>("Task deleted", HttpStatus.OK);
    }

    //delete all completed tasks
    @DeleteMapping("deleteCompletedTasks")
    public ResponseEntity<String> deleteCompletedTasks() {
        taskService.deleteCompletedTasks();
        return new ResponseEntity<>("All completed Tasks deleted", HttpStatus.OK);
    }

    //test methods
    @GetMapping("TEST")
    public void test() {
        //
    }
}
