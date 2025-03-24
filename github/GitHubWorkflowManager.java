package com.ElEmberator.themebuilder.github;

import com.ElEmberator.themebuilder.model.GitHubConfig;
import com.google.gson.JsonObject;
import okhttp3.*;

public class GitHubWorkflowManager {
    private final GitHubConfig config;
    private final OkHttpClient httpClient;

    public GitHubWorkflowManager(GitHubConfig config) {
        this.config = config;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public void triggerWorkflow(String inputValue) throws IOException {
        String apiUrl = config.getApiBase() + "/repos/" + config.getRepository() + 
                       "/actions/workflows/" + config.getWorkflowId() + "/dispatches";
        
        JsonObject inputs = new JsonObject();
        inputs.addProperty(config.getWorkflowInputName(), inputValue);
        
        JsonObject payload = new JsonObject();
        payload.addProperty("ref", "main");
        payload.add("inputs", inputs);
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(payload.toString(), MediaType.get("application/json")))
                .header("Authorization", "token " + config.getAuthToken())
                .header("Accept", "application/vnd.github.v3+json")
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 204) {
                throw new IOException("Workflow trigger failed: " + response.code());
            }
        }
    }
}
