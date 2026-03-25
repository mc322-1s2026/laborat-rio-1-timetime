package com.nexus.model;

import java.util.List;

import com.nexus.service.Workspace;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.trim().isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        } 
        else if (email == null || email.trim().isBlank()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.");
        } 
        else if ( ! email.trim().matches("^[\\w.-_]+@\\w+\\.[\\a-z]+")) {
            throw new IllegalArgumentException(
                String.format(
                    "O e-mail deve estar no formato \"usuario@dominio.com\". Inserido: \'%s\'."
                    ,email.trim()));
        }
        this.username = username.trim();
        this.email = email.trim();
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload() {
        List<Task> workspaceTasks = new Workspace().getTasks();
        return 
        workspaceTasks
        .stream()
        .filter(Task -> Task.getStatus() == TaskStatus.IN_PROGRESS)
        .filter(Task -> Task.getOwner().consultUsername() == this.consultUsername())
        .map(Task::getId)
        .distinct()
        .count(); 
    }
}