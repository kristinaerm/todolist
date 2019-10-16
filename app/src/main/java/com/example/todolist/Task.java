package com.example.todolist;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

@IgnoreExtraProperties
public class Task {

    public static final int EXPIRED = R.string.expired;
    public static final int TODAY = R.string.today;
    public static final int TOMORROW = R.string.tomorrow;
    public static final int LATER = R.string.later;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm");

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
        Date date = SIMPLE_DATE_FORMAT.parse(timeDate);
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
        Date date1 = SIMPLE_DATE_FORMAT.parse(timeDate);
        Date date2 = SIMPLE_DATE_FORMAT.parse(task.timeDate);
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
        for (int out = tasks.size() - 1; out >= 1; out--) {
            for (int in = 0; in < out; in++) {
                if (tasks.get(in).compare(tasks.get(in + 1)) == -1) {
                    bucket = tasks.get(in);
                    tasks.remove(in);
                    tasks.add(in + 1, bucket);
                }

            }
        }
        return tasks;
    }

    public int timeline() throws ParseException {
        Calendar today = Calendar.getInstance();
        Calendar thisDate = Calendar.getInstance();
        thisDate.setTime(SIMPLE_DATE_FORMAT.parse(timeDate));
        //year
        if (today.get(Calendar.YEAR) > thisDate.get(Calendar.YEAR)) {
            return EXPIRED;
        } else if (today.get(Calendar.YEAR) < thisDate.get(Calendar.YEAR)) {
            return LATER;
        } else {
            //month
            if (today.get(Calendar.MONTH) > thisDate.get(Calendar.MONTH)) {
                return EXPIRED;
            } else if (today.get(Calendar.MONTH) < thisDate.get(Calendar.MONTH)) {
                if (thisDate.get(Calendar.MONTH) - today.get(Calendar.MONTH) == 1) {
                    int iYear = today.get(Calendar.YEAR);
                    int iMonth = today.get(Calendar.MONTH); // 1 (months begin with 0)
                    int iDay = 1;
                    Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
                    int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if ((today.get(Calendar.DAY_OF_MONTH) == daysInMonth) && (thisDate.get(Calendar.DAY_OF_MONTH) == 1)) {
                        return TOMORROW;
                    }
                }
                return LATER;
            } else {
                //day
                if ((today.get(Calendar.DAY_OF_MONTH) > thisDate.get(Calendar.DAY_OF_MONTH))) {
                    return EXPIRED;
                } else if (today.get(Calendar.DAY_OF_MONTH) < thisDate.get(Calendar.DAY_OF_MONTH)) {
                    if (thisDate.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH) == 1) {
                        return TOMORROW;
                    }
                    return LATER;
                } else {
                    if ((today.get(Calendar.HOUR_OF_DAY) > thisDate.get(Calendar.HOUR_OF_DAY))
                            || ((today.get(Calendar.HOUR_OF_DAY) == thisDate.get(Calendar.HOUR_OF_DAY))
                            && (today.get(Calendar.MINUTE) > thisDate.get(Calendar.MINUTE)))) {
                        return EXPIRED;
                    }
                    return TODAY;
                }
            }
        }
    }
}
