package org.acme.TaskManagerRESTapi.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    //@JsonIgnore //Id wird nicht an API übergeben übergeben
    private Long id;
    private String description;
    private LocalDate dueDate;
    private Boolean isCompleted = false;
}
