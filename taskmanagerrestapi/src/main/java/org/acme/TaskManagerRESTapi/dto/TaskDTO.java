package org.acme.TaskManagerRESTapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//data transfer object for task
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private String description;
    private LocalDate dueDate;
}
