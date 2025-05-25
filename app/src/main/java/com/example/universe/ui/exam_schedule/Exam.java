package com.example.universe.ui.exam_schedule;

public class Exam implements Comparable<Exam> {
    private String name;
    private String date;
    private String key;

    public Exam() {
        // Firebase için gereklidir
    }

    public Exam(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(Exam other) {
        return this.date.compareTo(other.date);
    }
}
