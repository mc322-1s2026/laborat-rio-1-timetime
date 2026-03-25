package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/* LogProcess Class
* Responsável por processar um arquivo de log de ações (log.txt) e executar as operações correspondentes no motor Nexus.
*/
public class LogProcessor {

    /* processLog lê um arquivo de log linha por linha, interpreta as ações e executa as operações correspondentes no motor Nexus.
    * @param fileName Nome do arquivo de log a ser processado (deve estar na pasta de recursos do projeto)
    * @param workspace Instância do Workspace onde as tarefas serão gerenciadas
    * @param users Lista de usuários do sistema, onde novos usuários serão adicionados
    * @param projects Lista de projetos do sistema, onde novos projetos serão adicionados
    */
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
                                try{
                                    users.add(new User(p[1], p[2]));
                                    System.out.println("[LOG] Usuário criado: " + p[1]);
                                } catch(IllegalArgumentException e) {
                                    System.err.println("[ERRO] Não foi possível criar o usuário: " + e.getMessage());
                                    break;
                                }
                            }
                            case "CREATE_PROJECT" -> { 
                                try {
                                    // CREATE_PROJECT;projectName;budgetHours
                                    projects.add(new Project(p[1], Integer.parseInt(p[2])));
                                    System.out.println("[LOG] Projeto criado: " + p[1]);
                                } catch(NumberFormatException e) {
                                    System.err.println("[ERRO] Não foi possível criar o projeto: " + e.getMessage());
                                    break;
                                }
                            }
                            case "CREATE_TASK" -> { 
                                // CREATE_TASK;taskName;deadline;effort;projectName
                                try{
                                    Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]), p[4]);
                                    workspace.addTask(t);
                                    Project project = projects.stream()
                                        .filter(proj -> proj.getProjectName().equals(p[4]))
                                        .findFirst()
                                        .orElse(null);
                                    project.addTask(t);
                                    System.out.println("[LOG] Tarefa criada: " + p[1]);
                                } catch(NumberFormatException e) {
                                    System.err.println("[ERRO] Não foi possível criar a tarefa: " + e.getMessage());
                                    break;
                                } 
                            }
                            case "ASSIGN_USER" -> {
                                // ASSIGN_USER;taskId;username
                                try {
                                    Task task = workspace.getTaskById(Integer.parseInt(p[1]));
                                    User user = users.stream()
                                        .filter(u -> u.consultUsername().equals(p[2]))
                                        .findFirst()
                                        .orElse(null);
                                    task.setOwner(user);
                                } catch(NexusValidationException e) {
                                    System.err.println("[ERRO] Não foi possível definir o owner da task: " + e.getMessage());
                                }
                            }
                            case "CHANGE_STATUS" -> {
                                // CHANGE_STATUS;taskId;newStatus
                                try {
                                    Task task = workspace.getTaskById(Integer.parseInt(p[1]));
                                    if (task == null) {
                                        System.err.println("[ERRO] Tarefa com ID " + p[1] + " não encontrada.");
                                        break;
                                    }
                                    if (TaskStatus.valueOf(p[2]).equals(task.getStatus())) {
                                        break;
                                    }
                                    else if (TaskStatus.valueOf(p[2]).equals(TaskStatus.IN_PROGRESS)) {
                                        task.moveToInProgress(task.getOwner());
                                    } 
                                    else if (TaskStatus.valueOf(p[2]).equals(TaskStatus.DONE)) {
                                        task.markAsDone();
                                    } 
                                    else if (TaskStatus.valueOf(p[2]).equals(TaskStatus.BLOCKED)) {
                                        task.setBlocked(task.getStatus() == TaskStatus.BLOCKED);
                                    } 
                                } catch(NexusValidationException e) {
                                    System.err.println("[ERRO] Não foi possível alterar o status da tarefa: " + e.getMessage());
                                }

                            }
                            case "REPORT_STATUS" -> {
                                // REPORT_STATUS: Aciona a impressão dos relatórios analíticos (Streams) no console.
                                System.out.println("Top Performers: " + workspace.topPerformers());
                                System.out.println("Overloaded Users: " + workspace.overloadedUsers());
                                System.out.println("Project Health: " + workspace.projectHealth());
                                System.out.println("Global Bottlenecks: " + workspace.globalBottlenecks());
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