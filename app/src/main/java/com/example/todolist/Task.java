package com.example.todolist;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Task {
    private String name;
    private String idTask;
    private String idCategory;
    private String timeDate;

    public Task() {

    }

    public Task(String idTask,String name, String idCategory, String timeDate) {

        this.name = name;
        this.idTask = idTask;
        this.idCategory = idCategory;
        this.timeDate = timeDate;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIdTask() {
        return idTask;
    }

    public void setIdTask(String idTask) {
        this.idTask = idTask;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getTimeDate() {
        return this.timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

}
