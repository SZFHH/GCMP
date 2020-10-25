package com.haha.gcmp.model.entity;

import java.util.Comparator;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public class DataFile implements Comparator<DataFile> {
    private boolean file;

    private String name;

    private long size;

    public DataFile() {
    }

    public DataFile(boolean file, String name, long size) {
        this.file = file;
        this.name = name;
        this.size = size;
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
    public int compare(DataFile leftFile, DataFile rightFile) {
        if (leftFile.file && !rightFile.file) {
            return 1;
        }

        if (!leftFile.file && rightFile.file) {
            return -1;
        }

        return leftFile.getName().compareTo(rightFile.getName());
    }
}
