package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;               // Imutável após o nascimento
    private final LocalDate deadline;   // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    private int estimatedEffort;

    public Task(String title, LocalDate deadline) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload
        // Se falhar, incrementar totalValidationErrors e lançar NexusValidationException
        if(user.consultUsername().isBlank() &&  this.owner.consultUsername().isBlank()){
            totalValidationErrors++;
            throw new NexusValidationException("Não é possível atribuir uma tarefa sem especificar um owner.");
        } else if (this.status == TaskStatus.BLOCKED){
            totalValidationErrors++;
            throw new NexusValidationException(String.format(
                "A tarefa %i está bloqueada. Não é possível alterar o status para \'Em progresso\'.", this.id));
        }

        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;

        activeWorkload++;

    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload (decrementar)
        if (this.status == TaskStatus.BLOCKED){
            totalValidationErrors++;
            throw new NexusValidationException(String.format(
                "A tarefa %i está bloqueada. Não é possível alterar o status para \'Finalizada\'.", this.id));
        }

        // this.owner = null; (validar se é necessario essa condição)
        this.status = TaskStatus.DONE;

        activeWorkload--;
    }

    public void setBlocked(boolean blocked) {
        if (blocked && this.status != TaskStatus.DONE) { //Só é possível mudar para blocked se não estiver em DONE
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { 
        return id; 
    }

    public TaskStatus getStatus() { 
        return status; 
    }

    public String getTitle() { 
        return title; 
    }

    public LocalDate getDeadline() { 
        return deadline; 
    }

    public User getOwner() { 
        return owner; 
    }

    public int getEstimatedEffort() {
        return estimatedEffort;
    }
}