package com.ElEmberator.themebuilder.github;

import com.ElEmberator.themebuilder.model.GitHubConfig;
import okhttp3.*;
import java.io.IOException;

public class GitHubClient {
    private final GitHubConfig config;
    private final OkHttpClient httpClient;

    public GitHubClient(GitHubConfig config) {
        this.config = config;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public void uploadFile(String path, String contentBase64, String commitMessage) throws IOException {
        String apiUrl = config.getApiBase() + "/repos/" + config.getRepository() + "/contents/" + path;
        
        String payload = String.format("{\"message\":\"%s\",\"content\":\"%s\"}", 
            commitMessage, contentBase64);
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .put(RequestBody.create(payload, MediaType.get("application/json")))
                .header("Authorization", "token " + config.getAuthToken())
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.code());
            }
        }
    }

    public String getFileContent(String path) throws IOException {
        String apiUrl = config.getApiBase() + "/repos/" + config.getRepository() + "/contents/" + path;
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "token " + config.getAuthToken())
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Download failed: " + response.code());
            }
            return response.body().string();
        }
    }
}
