package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.User;
import com.nexus.model.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    // public List<User> topPerformers() {
    //     // Retorna top 3 usuários com mais tarefas concluídas (STATUS == DONE)
    // }

    public List<User> overloadedUsers() {
        // Retorna todos usuários com carga de trabalho > 10 tasks com STATUS == in PROGRESS
        return tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
            .map(Task::getOwner)
            .filter(u -> u.calculateWorkload() > 10)
            .distinct()
            .toList();
    }

    public float projectHealth() {
        // Retorna percentual de conclusão do projeto, ou seja, tasks DONE / total tasks
        long doneTasks = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();
        
        long totalTasks = tasks.stream()
            .count();
        
        return (doneTasks / totalTasks);
    }

    // public TaskStatus globalBottlenecks() {
    //     // Verificar qual status possui maior n de tarefas no sistema - {DONE}
    // }
}