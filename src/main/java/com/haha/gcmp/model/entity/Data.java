package com.haha.gcmp.model.entity;

import java.util.Comparator;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public class Data implements Comparator<Data> {
    private boolean file;

    private String name;

    private long size;

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Data() {
    }

    public Data(boolean file, String name, long size, String path) {
        this.file = file;
        this.name = name;
        this.size = size;
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compare(Data leftFile, Data rightFile) {
        if (leftFile.file && !rightFile.file) {
            return 1;
        }

        if (!leftFile.file && rightFile.file) {
            return -1;
        }

        return leftFile.getName().compareTo(rightFile.getName());
    }
}
