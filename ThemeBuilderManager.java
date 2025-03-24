package com.ElEmberator.themebuilder;

import android.content.Context;
import com.ElEmberator.themebuilder.builder.ThemeGenerator;
import com.ElEmberator.themebuilder.builder.ZipPackager;
import com.ElEmberator.themebuilder.github.GitHubClient;
import com.ElEmberator.themebuilder.github.GitHubWorkflowManager;
import com.ElEmberator.themebuilder.model.GitHubConfig;
import com.ElEmberator.themebuilder.model.ThemeConfig;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class ThemeBuilderManager {
    private static final int MAX_POLL_ATTEMPTS = 20;
    private static final long POLL_INTERVAL_MS = 15000;

    private final Context context;
    private final ThemeConfig themeConfig;
    private final GitHubConfig gitHubConfig;
    private final ThemeGenerator themeGenerator;
    private final ZipPackager zipPackager;
    private final GitHubClient gitHubClient;
    private final GitHubWorkflowManager workflowManager;

    public ThemeBuilderManager(Context context, String githubToken) {
        this.context = context;
        this.themeConfig = new ThemeConfig();
        this.gitHubConfig = new GitHubConfig(githubToken);
        
        this.themeGenerator = new ThemeGenerator(context, themeConfig);
        this.zipPackager = new ZipPackager();
        this.gitHubClient = new GitHubClient(gitHubConfig);
        this.workflowManager = new GitHubWorkflowManager(gitHubConfig);
    }

    public ThemeConfig getThemeConfig() {
        return themeConfig;
    }

    public void buildAndDeployTheme(String themeName, ThemeDeploymentCallback callback) {
        new Thread(() -> {
            try {
                validateThemeName(themeName);
                File themeDir = createTempDir();
                File themeZip = new File(context.getCacheDir(), themeName + ".zip");
                
                themeGenerator.generateThemeFiles(themeDir);
                zipPackager.packageTheme(themeDir, themeZip);
                
                byte[] zipContent = readFileToBytes(themeZip);
                String base64Content = Base64.getEncoder().encodeToString(zipContent);
                
                gitHubClient.uploadFile("themes/" + themeName + ".zip", base64Content, 
                    "Add theme " + themeName);
                
                workflowManager.triggerWorkflow(themeName);
                
                File apkFile = pollForArtifact(themeName);
                deleteDirectory(themeDir);
                
                callback.onSuccess(apkFile);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    private File pollForArtifact(String themeName) throws Exception {
        int attempts = 0;
        while (attempts < MAX_POLL_ATTEMPTS) {
            try {
                String artifactsJson = gitHubClient.getFileContent("actions/artifacts");
                // Parse JSON to find artifact and download URL
                // This is simplified - in real implementation parse the JSON properly
                if (artifactsJson.contains(themeName + "-theme")) {
                    String downloadUrl = extractDownloadUrl(artifactsJson, themeName);
                    return downloadArtifact(downloadUrl, themeName);
                }
            } catch (IOException e) {
                // Continue polling
            }
            
            attempts++;
            TimeUnit.MILLISECONDS.sleep(POLL_INTERVAL_MS);
        }
        throw new IOException("APK not found after " + MAX_POLL_ATTEMPTS + " attempts");
    }

    private File downloadArtifact(String downloadUrl, String themeName) throws IOException {
        File outputDir = new File(context.getExternalFilesDir(null), "KeyboardThemes");
        if (!outputDir.exists()) outputDir.mkdirs();
        
        File apkFile = new File(outputDir, themeName + ".apk");
        
        Request request = new Request.Builder()
                .url(downloadUrl)
                .header("Authorization", "token " + gitHubConfig.getAuthToken())
                .build();
        
        try (Response response = gitHubClient.getHttpClient().newCall(request).execute();
             InputStream is = response.body().byteStream();
             FileOutputStream fos = new FileOutputStream(apkFile)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        
        return apkFile;
    }

    private String extractDownloadUrl(String artifactsJson, String themeName) {
        // Simplified - in real implementation parse JSON properly
        return gitHubConfig.getApiBase() + "/repos/" + gitHubConfig.getRepository() + 
               "/actions/artifacts/12345/zip"; // Would extract real ID from JSON
    }

    private File createTempDir() throws IOException {
        File tempDir = new File(context.getCacheDir(), "theme_temp");
        if (!tempDir.exists()) tempDir.mkdirs();
        return tempDir;
    }

    private byte[] readFileToBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    private boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        return directory.delete();
    }

    private void validateThemeName(String themeName) throws IllegalArgumentException {
        if (themeName == null || themeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Theme name cannot be empty");
        }
        if (!themeName.matches("^[a-zA-Z0-9_-]{3,50}$")) {
            throw new IllegalArgumentException(
                "Theme name must be 3-50 characters and contain only letters, numbers, hyphens and underscores"
            );
        }
    }

    public interface ThemeDeploymentCallback {
        void onSuccess(File apkFile);
        void onError(Exception e);
    }
}
