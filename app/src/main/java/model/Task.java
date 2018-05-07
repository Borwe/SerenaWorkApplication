package model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task_db")
public class Task implements Serializable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    private long task_id;

    @ColumnInfo(name = "task_date")
    private String task_date;

    @ColumnInfo(name = "tasl_text")
    private String task_text;

    @ColumnInfo(name = "task_sent")
    private boolean task_sent;

    public Task(String task_date,String task_text){
        this.task_date=task_date;
        this.task_text=task_text;
        this.task_sent=false;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

    public String getTask_text() {
        return task_text;
    }

    public void setTask_text(String task_text) {
        this.task_text = task_text;
    }

    public boolean isTask_sent() {
        return task_sent;
    }

    public void setTask_sent(boolean task_sent) {
        this.task_sent = task_sent;
    }

    @Override
    public String toString() {
        return "Task{" +
                "task_id='" + task_id + '\'' +
                ", task_date='" + task_date + '\'' +
                ", task_text='" + task_text + '\'' +
                ", task_sent=" + task_sent +
                '}';
    }
}
