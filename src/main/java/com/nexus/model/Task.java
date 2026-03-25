package com.nexus.model;

import java.time.LocalDate;
import com.nexus.exception.NexusValidationException;

/**
 * Representa uma tarefa dentro do sistema Nexus.
 * Possui informações básicas como título, prazo, responsável e status.
 * Também mantém métricas globais relacionadas ao uso das tarefas.
 */
public class Task {

    /** Total de tarefas criadas. */
    public static int totalTasksCreated = 0;

    /** Total de erros de validação ocorridos. */
    public static int totalValidationErrors = 0;

    /** Carga de trabalho ativa (tarefas em progresso). */
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;
    private final LocalDate deadline;
    private String title;
    private TaskStatus status;
    private User owner;
    private int estimatedEffort;
    private String projectName;

    /**
     * Cria uma nova tarefa com status inicial TO_DO.
     *
     * @param title título da tarefa
     * @param deadline prazo de entrega
     * @param estimatedEffort esforço estimado
     * @param projectName nome do projeto
     */
    public Task(String title, LocalDate deadline, int estimatedEffort, String projectName) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        this.estimatedEffort = estimatedEffort;
        this.projectName = projectName;

        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Requer um usuário válido e que a tarefa não esteja bloqueada.
     *
     * @param user usuário responsável
     * @throws NexusValidationException se não houver owner válido ou se estiver bloqueada
     */
    public void moveToInProgress(User user) {
        if(user.consultUsername().isBlank() && this.owner.consultUsername().isBlank()){
            totalValidationErrors++;
            throw new NexusValidationException("Não é possível atribuir uma tarefa sem especificar um owner.");
        } 
        else if (this.status == TaskStatus.BLOCKED){
            totalValidationErrors++;
            throw new NexusValidationException(String.format(
                "A tarefa %i está bloqueada. Não é possível alterar o status para 'Em progresso'.", this.id));
        }

        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }

    /**
     * Marca a tarefa como concluída (DONE).
     * Não é permitido concluir tarefas bloqueadas.
     *
     * @throws NexusValidationException se a tarefa estiver bloqueada
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED){
            totalValidationErrors++;
            throw new NexusValidationException(String.format(
                "A tarefa %i está bloqueada. Não é possível alterar o status para 'Finalizada'.", this.id));
        }

        this.status = TaskStatus.DONE;
        activeWorkload--;
    }

    /**
     * Define ou remove o estado de bloqueio da tarefa.
     *
     * @param blocked true para bloquear, false para retornar ao estado TO_DO
     */
    public void setBlocked(boolean blocked) {
        if (blocked && this.status != TaskStatus.DONE) {
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO;
        }
    }

    /**
     * Define o responsável pela tarefa.
     *
     * @param user usuário responsável
     */
    public void setOwner(User user) {
        this.owner = user;
    }

    /** @return identificador único da tarefa */
    public int getId() { 
        return id; 
    }

    /** @return status atual da tarefa */
    public TaskStatus getStatus() { 
        return status; 
    }

    /** @return título da tarefa */
    public String getTitle() { 
        return title; 
    }

    /** @return prazo da tarefa */
    public LocalDate getDeadline() { 
        return deadline; 
    }

    /** @return responsável pela tarefa */
    public User getOwner() { 
        return owner; 
    }

    /** @return nome do projeto */
    public String getProjectName() {
        return projectName;
    }

    /** @return esforço estimado */
    public int getEstimatedEffort() {
        return estimatedEffort;
    }
}