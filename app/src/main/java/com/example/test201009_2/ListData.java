package com.example.test201009_2;

public class ListData {
    private String filename;
    private int count;


    public ListData(String filename, int count) {
        this.filename = filename;
        this.count = count;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
