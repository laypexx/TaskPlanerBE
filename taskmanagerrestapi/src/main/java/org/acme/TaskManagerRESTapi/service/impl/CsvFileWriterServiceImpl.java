package org.acme.TaskManagerRESTapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.acme.TaskManagerRESTapi.service.CsvFileWriterService;
import org.acme.TaskManagerRESTapi.models.Task;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.List;

@Slf4j
@Service
public class CsvFileWriterServiceImpl implements CsvFileWriterService {
    private static final String delimiter = ";";

    public void writeTaskToCsv(List<Task> taskList, String filePath) {
        BufferedWriter writer = null;
        try {
            File file = new File(filePath);
            writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8));
            if (file.length() == 0) {
                StringJoiner sjHeader = new StringJoiner(delimiter);
                sjHeader.add("ID");
                sjHeader.add("Description");
                sjHeader.add("dueDate");
                sjHeader.add("isCompleted");
                writer.write(sjHeader.toString() + "\r\n");
            }
            for (Task task : taskList) {
                StringJoiner sjTask = new StringJoiner(delimiter);
                sjTask.add(task.getId().toString());
                sjTask.add(task.getDescription().replace(';',','));
                sjTask.add(task.getDueDate().toString());
                sjTask.add(task.getIsCompleted().toString());
                writer.write(sjTask.toString() + "\r\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
