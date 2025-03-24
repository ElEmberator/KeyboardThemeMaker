package com.ElEmberator.themebuilder.builder;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipPackager {
    public void packageTheme(File sourceDir, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            addDirectoryToZip(sourceDir, zos, "res/drawable/");
        }
    }

    private void addDirectoryToZip(File directory, ZipOutputStream zos, String zipPath) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        byte[] buffer = new byte[1024];
        
        for (File file : files) {
            if (file.isDirectory()) {
                addDirectoryToZip(file, zos, zipPath + file.getName() + "/");
                continue;
            }
            
            try (FileInputStream fis = new FileInputStream(file)) {
                zos.putNextEntry(new ZipEntry(zipPath + file.getName()));
                
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                
                zos.closeEntry();
            }
        }
    }
}
