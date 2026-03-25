package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.User;
import com.nexus.model.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Representa um workspace contendo um conjunto de tarefas.
 * Fornece operações de consulta e métricas sobre as tarefas.
 */
public class Workspace {

    private final List<Task> tasks = new ArrayList<>();

    /**
     * Adiciona uma nova tarefa ao workspace.
     *
     * @param task tarefa a ser adicionada
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Retorna uma lista imutável das tarefas.
     *
     * @return lista de tarefas não modificável
     */
    public List<Task> getTasks() {
        return List.copyOf(Collections.unmodifiableList(tasks));
    }

    /**
     * Busca uma tarefa pelo seu identificador.
     *
     * @param id identificador da tarefa
     * @return tarefa encontrada ou null se não existir
     */
    public Task getTaskById(int id) {
        return tasks.stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Retorna os 3 usuários com maior número de tarefas concluídas.
     *
     * @return lista dos usuários com melhor desempenho
     */
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

    /**
     * Identifica usuários com carga de trabalho acima do limite.
     *
     * @return lista de usuários sobrecarregados
     */
    public List<User> overloadedUsers() {
        return tasks.stream()
            .filter(t -> t.getStatus().equals(TaskStatus.IN_PROGRESS))
            .map(Task::getOwner)
            .filter(u -> u.calculateWorkload() > 10)
            .distinct()
            .toList();
    }

    /**
     * Calcula a saúde do projeto com base na proporção de tarefas concluídas.
     *
     * @return valor entre 0 e 1 representando o progresso do projeto
     */
    public float projectHealth() {
        long doneTasks = tasks.stream()
            .filter(t -> t.getStatus().equals(TaskStatus.DONE))
            .count();

        long totalTasks = tasks.size();

        return (doneTasks / totalTasks);
    }

    /**
     * Identifica o status mais frequente entre tarefas não concluídas,
     * indicando possível gargalo.
     *
     * @return status predominante ou null se não houver tarefas
     */
    public TaskStatus globalBottlenecks() {
        return tasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.DONE)
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}