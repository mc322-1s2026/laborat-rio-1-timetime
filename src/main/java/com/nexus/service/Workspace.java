package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.User;
import com.nexus.model.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public Task getTaskById(int id) {
        return tasks.stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<User> topPerformers() {
        return tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)           
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<User, Long>comparingByValue().reversed()) 
            .limit(3)                                                
            .map(Map.Entry::getKey)                                   
            .toList();                                               
    }

    public List<User> overloadedUsers() {
        return tasks.stream()
            .filter(t -> t.getStatus().equals(TaskStatus.IN_PROGRESS))
            .map(Task::getOwner)
            .filter(u -> u.calculateWorkload() > 10)    
            .distinct()
            .toList();
    }

    public float projectHealth() {
        long doneTasks = tasks.stream().filter(t -> t.getStatus().equals(TaskStatus.DONE)).count();
        
        long totalTasks = tasks.size();
        
        return (doneTasks / totalTasks);
    }

    public TaskStatus globalBottlenecks() {
        return tasks.stream()
        .filter(t -> t.getStatus() != TaskStatus.DONE)
        .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
        .entrySet().stream().max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);    
    }
}