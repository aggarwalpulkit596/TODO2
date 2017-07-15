package com.example.pulkit.todo;

import java.io.Serializable;
import java.util.ArrayList;


class Todo implements Serializable {

    int id;
    String title;
    String description;
    String date;
    String time;
    int lastclicked=-1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    int category;
    int priority;


    public Todo(int id,String title,String description,int priority,int category,String date,String time){
        this.id=id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.date = date;
        this.time = time;

    }

}
