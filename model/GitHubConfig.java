package com.ElEmberator.themebuilder.model;

public class GitHubConfig {
    private String apiBase = "https://api.github.com";
    private String repository = "your-username/keyboard-theme-builder";
    private String workflowId = "build-theme.yml";
    private String workflowInputName = "theme_name";
    private String authToken;

    public GitHubConfig(String authToken) {
        this.authToken = authToken;
    }

    public String getApiBase() { return apiBase; }
    public String getRepository() { return repository; }
    public String getWorkflowId() { return workflowId; }
    public String getWorkflowInputName() { return workflowInputName; }
    public String getAuthToken() { return authToken; }
}
