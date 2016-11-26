package com.wuyz.copyandroidproject;

import java.io.*;

public class Main {

    private static int count = 0;
    private static final String[] blackNames = new String[] {"bin", "build", "gen", ".git", ".svn"};

    public static void main(String[] args) {
	    if (args.length != 2) {
            System.err.println("input error! usage: copyandroidproject srcDir destDir");
            return;
        }
        File srcPath = new File(args[0]);
        if (!srcPath.exists()) {
            System.err.println("file or path not exist: " + args[0]);
            return;
        }
        File destPath = new File(args[1]);
        if (destPath.exists() && destPath.isFile()) {
            System.err.println("dest path is a file: " + args[1]);
            return;
        }
        long startTime = System.currentTimeMillis();
        count = 0;
        tryCopyFile(srcPath, destPath);
        long costTime = System.currentTimeMillis() - startTime;
        System.out.println("total copy " + count + " files cost " + costTime / 1000 + "s");
    }

    private static void tryCopyFile(File srcFile, File destPath) {
//        System.out.println("tryCopyFile: " + srcFile.getAbsolutePath() + " -> " + destPath.getAbsolutePath());
        if (!srcFile.exists())
            return;
        if (srcFile.isFile()) {
            if (!destPath.exists()) {
                if (!destPath.mkdirs()) {
                    System.err.println("can not mkdirs: " + destPath.getAbsolutePath());
                    return;
                }
            }
            String name = srcFile.getName();
            File destFile;
            if (destPath.isDirectory())
                destFile = new File(destPath, name);
            else
                destFile = destPath;
            copyFile(srcFile, destFile);
        } else {
            boolean isBlack = false;
            final String name = srcFile.getName();
            for (String s : blackNames) {
                if (s.equals(name)) {
                    isBlack = true;
                    break;
                }
            }
            if (isBlack)
                return;
            File[] files = srcFile.listFiles();
            if (files == null || files.length == 0)
                return;
            File newDest = new File(destPath, name);
            for (File file : files) {
                tryCopyFile(file, newDest);
            }
        }
    }

    private static boolean copyFile(File srcFile, File destFile) {
//        System.out.println("copy file: " + srcFile.getAbsolutePath() + " -> " + destFile.getAbsolutePath());
        System.out.println(srcFile.getAbsolutePath());
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(srcFile));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile, false))) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }
            count++;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
