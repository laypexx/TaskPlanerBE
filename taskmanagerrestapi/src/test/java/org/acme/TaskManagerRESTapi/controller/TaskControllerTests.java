package org.acme.TaskManagerRESTapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.acme.TaskManagerRESTapi.dto.TaskDTO;
import org.acme.TaskManagerRESTapi.models.Task;
import org.acme.TaskManagerRESTapi.service.TaskService;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TaskControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;
    private Task task;
    private TaskDTO taskDto;

    @BeforeEach
    public void init() {
        task = Task.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();
        taskDto = TaskDTO.builder()
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-10")).build();
    }

    @Test
    public void TaskController_CreateTask_ReturnCreated() throws Exception {
        given(taskService.createTask(ArgumentMatchers.any())).willAnswer((invocation -> invocation.getArgument(0)));

        ResultActions response = mockMvc.perform(post("/tasks/createTask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));

        response.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(taskDto.getDescription())));
        //.andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", CoreMatchers.is(taskDto.getDueDate())));
    }

    @Test
    public void TaskController_GetAllTasks_ReturnResponseDto() throws Exception {
        ResultActions response = mockMvc.perform(get("/tasks/getAllTasks"));

        response.andExpect(status().isOk());
    }

    @Test
    public void TaskController_ReadTaskById_ReturnTaskDto() throws Exception {
        when(taskService.readTask(1L)).thenReturn(taskDto);

        ResultActions response = mockMvc.perform(get("/tasks/readTask/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(taskDto.getDescription())));
        //.andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", CoreMatchers.is(taskDto.getDueDate())));
    }

    @Test
    public void TaskController_UpdateTask_ReturnTaskDto() throws Exception {
        when(taskService.updateTask(1L, taskDto)).thenReturn(taskDto);

        ResultActions response = mockMvc.perform(put("/tasks/updateTask/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(taskDto.getDescription())));
        //.andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", CoreMatchers.is(taskDto.getDueDate())));
    }

    @Test
    public void TaskController_CompleteTask_ReturnTask() throws Exception {
        when(taskService.completeTask(1L)).thenReturn(taskDto);

        ResultActions response = mockMvc.perform(put("/tasks/completeTask/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(taskDto.getDescription())));
    }

    @Test
    public void TaskController_WriteCompletedTasksToCsv_ReturnString() throws Exception {
        doNothing().when(taskService).writeCompletedTasksToCsv();

        ResultActions response = mockMvc.perform(post("/tasks/writeCompletedTasksToCsv")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Completed Tasks successfully written to Csv"));
    }

    @Test
    public void TaskController_GetFileContent_ReturnString() throws Exception {
        String expectedContent = "This is a test file";
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "testfile.csv", "text/plain"
                        , expectedContent.getBytes(StandardCharsets.UTF_8));

        when(taskService.getFileContent(multipartFile)).thenReturn(expectedContent);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart("/tasks/getFileContent").file(multipartFile));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedContent));
    }

    @Test
    public void TaskController_DownloadResource_ReturnString() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(taskService).downloadResource(request, response);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks/downloadCompletedTasks")
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    public void TaskController_DeleteTaskById_ReturnString() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        ResultActions response = mockMvc.perform(delete("/tasks/deleteTask/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());
    }

    @Test
    public void TaskController_DeleteCompletedTasks_ReturnString() throws Exception {
        mockMvc.perform(delete("/tasks/deleteCompletedTasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteCompletedTasks();
    }
}
