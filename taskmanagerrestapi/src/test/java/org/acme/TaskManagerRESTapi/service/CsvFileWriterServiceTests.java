package org.acme.TaskManagerRESTapi.service;

import org.acme.TaskManagerRESTapi.models.Task;
import org.acme.TaskManagerRESTapi.service.impl.CsvFileWriterServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CsvFileWriterServiceTests {

    @InjectMocks
    private CsvFileWriterServiceImpl csvFileWriterService;

    @Test
    public void CsvFileWriterService_WriteTasksToCsv_ReturnFileIsNotNull() {
        String filePath = "./csvfiles/test.csv";
        Task task = Task.builder()
                .id(1L)
                .description("Hausaufgaben machen")
                .dueDate(LocalDate.parse("2023-10-16"))
                .isCompleted(true).build();

        List<Task> taskList = List.of(task);
        csvFileWriterService.writeTaskToCsv(taskList, filePath);

        File file = new File(filePath);
        Assertions.assertThat(file.exists()).isTrue();
        Assertions.assertThat(file.length()).isNotNull();
    }
}
