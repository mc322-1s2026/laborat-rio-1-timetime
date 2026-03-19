package com.nexus.model;
import java.util.ArrayList;
import java.util.List;
import com.nexus.exception.NexusValidationException;

public class Project {
    private String projectName;
    private List<Task> tasks = new ArrayList<>();
    private int totalBudget; // Horas de trabalho alocadas para o projeto

    public Project(String projectName, int totalBudget) {
        // Verify each attribute
        if (projectName == null || projectName.trim().isBlank()) {
            throw new NexusValidationException("O nome do projeto não pode ser vazio.");
        }
        if (tasks == null) {
            throw new NexusValidationException("O projeto deve possuir tasks.");
        }
        this.projectName = projectName;
        this.totalBudget = totalBudget;
    }

    public void addTask(Task new_task) {
        // Verify if the current tasks hours excced the budget.
        if (new_task == null) {
            throw new IllegalArgumentException("Tarefa não pode ser nula.");
        }

        if (tasks.stream().mapToDouble(task -> task.getEstimatedEffort()).sum() + new_task.getEstimatedEffort() > totalBudget) {
            throw new NexusValidationException("A soma das horas das tasks excederam o limite máximo de horas do projeto.");
        } else {
            tasks.add(new_task);
        }
    }

    // Getters
    public String getProjectName() {    
        return projectName;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public int getTotalBudget() {
        return totalBudget;
    }
}
