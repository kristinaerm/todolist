package com.example.todolist;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

@IgnoreExtraProperties
public class Task {

    public static final int EXPIRED = R.string.expired;
    public static final int TODAY = R.string.today;
    public static final int TOMORROW = R.string.tomorrow;
    public static final int LATER = R.string.later;

    private String name;
    private String idTask;
    private String idCategory;
    private String timeDate;


    public Task() {

    }

    public Task(String name, String idCategory, String timeDate) {

        this.name = name;
        this.idTask = idTask;
        this.idCategory = idCategory;
        this.timeDate = timeDate;

    }

    public Task(String idTask, String name, String idCategory, String timeDate) {

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


    public Calendar getTimeDateCalendar() throws ParseException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        Date date = sdf.parse(timeDate);
        c.setTime(date);
        return c;
    }

    public String getTimeDate() {
        return this.timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String toString() {
        return name + "\n" + timeDate;
    }

    public int compare(Task task) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        Date date1 = sdf.parse(timeDate);
        Date date2 = sdf.parse(task.timeDate);
        if (date2.after(date1)) {
            return -1;
        } else if (date2.before(date1)) {
            return 1;
        } else return 0;
    }

    public static LinkedList<Task> sort(LinkedList<Task> tasks) throws ParseException {
        Task bucket;
        if (tasks.size() < 2)
            return tasks;
        for (int out = tasks.size() - 1; out >= 1; out--) {  //Внешний цикл
            for (int in = 0; in < out; in++) {       //Внутренний цикл
                if (tasks.get(in).compare(tasks.get(in + 1)) == -1)               //Если порядок элементов нарушен
                {
                    bucket = tasks.get(in);
                    tasks.remove(in);
                    tasks.add(in + 1, bucket);
                }

            }
        }
        return tasks;
    }

    public int timeline() throws ParseException {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        Date thisDate = sdf.parse(timeDate);
        //year
        if (today.getYear() > thisDate.getYear()) {
            return EXPIRED;
        } else if (today.getYear() < thisDate.getYear()) {
            return LATER;
        } else {
            //month
            if (today.getMonth() > thisDate.getMonth()) {
                return EXPIRED;
            } else if (today.getMonth() < thisDate.getMonth()) {
                if (thisDate.getMonth() - today.getMonth() == 1) {
                    int iYear = today.getYear();
                    int iMonth = today.getMonth(); // 1 (months begin with 0)
                    int iDay = 1;
                    Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
                    int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if ((today.getDate() == daysInMonth) && (thisDate.getDate() == 1)) {
                        return TOMORROW;
                    }
                }
                return LATER;
            } else {
                //day
                if ((today.getDate() > thisDate.getDate())||(today.getHours()>thisDate.getHours())||((today.getHours()==thisDate.getHours())&&(today.getMinutes()>thisDate.getMinutes()))) {
                    return EXPIRED;
                } else if (today.getDate() < thisDate.getDate()) {
                    if (thisDate.getDate() - today.getDate() == 1) {
                        return TOMORROW;
                    }
                    return LATER;
                } else {
                    return TODAY;
                }
            }
        }
    }
}
