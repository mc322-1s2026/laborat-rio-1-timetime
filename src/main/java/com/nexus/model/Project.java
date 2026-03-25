package com.nexus.model;
import java.util.ArrayList;
import java.util.List;
import com.nexus.exception.NexusValidationException;

/* Projeto representa um conjunto de tarefas relacionadas, com um orçamento total de horas alocadas.
* @author Fernando Fugihara
* @version 1.0
*/
public class Project {
    final private String projectName;
    private List<Task> tasks = new ArrayList<>();
    final private int totalBudget; // Horas de trabalho alocadas para o projeto

    /* Construtor do Projeto.
    * @param projectName Nome do projeto 
    * @param totalBudget Orçamento total de horas para o projeto
     */
    public Project(String projectName, int totalBudget) {
        if (projectName == null || projectName.trim().isBlank()) {
            throw new NexusValidationException("O nome do projeto não pode ser vazio.");
        }
        if (tasks == null) {
            throw new NexusValidationException("O projeto deve possuir tasks.");
        }
        this.projectName = projectName;
        this.totalBudget = totalBudget;
    }


    /* addTask adiciona uma nova tarefa ao projeto, garantindo que o orçamento total de horas não seja excedido.
    - @param new_task A tarefa a ser adicionada ao projeto
    - Valida se a tarefa é nula ou se a adição da tarefa excederia o orçamento total do projeto.
    */
    public void addTask(Task new_task) {
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
