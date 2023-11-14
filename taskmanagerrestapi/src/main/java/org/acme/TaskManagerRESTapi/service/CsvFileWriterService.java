package org.acme.TaskManagerRESTapi.service;

import org.acme.TaskManagerRESTapi.models.Task;

import java.util.List;

public interface CsvFileWriterService {
    void writeTaskToCsv(List<Task> taskList, String filePath);
}
