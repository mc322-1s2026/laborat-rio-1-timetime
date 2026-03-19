package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users, List<Project> projects) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> { 
                                // CREATE_USER;username;email
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> { 
                                // CREATE_PROJECT;projectName;budgetHours
                                projects.add(new Project(p[1], Integer.parseInt(p[2])));
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> { 
                                // CREATE_TASK;taskName;deadline;effort;projectName
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]), p[4]);
                                workspace.addTask(t);
                                Project project = projects.stream()
                                    .filter(proj -> proj.getProjectName() == p[4])
                                    .findFirst()
                                    .orElse(null);
                                project.addTask(t);
                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "ASSIGN_USER" -> {
                                // ASSIGN_USER;taskId;username
                                // Locate task through task ID and 
                                // user through username -> ENSURE Username to be UNIQUE! 
                                Task task = workspace.getTaskById(Integer.parseInt(p[1]));
                                User user = users.stream()
                                    .filter(u -> u.consultUsername() == p[2])
                                    .findFirst()
                                    .orElse(null);
                                task.setOwner(user);
                            }
                            case "CHANGE_STATUS" -> {
                                // CHANGE_STATUS;taskId;newStatus
                            }
                            case "REPORT_STATUS" -> {
                                // REPORT_STATUS: Aciona a impressão dos relatórios analíticos (Streams) no console.
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}