package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import model.Task;

@Dao
public interface TaskDao {

    @Insert
    public void putTask(Task task);

    @Query("select * from task_db")
    public List<Task> getTasks();

    @Delete
    public  void deleteTask(Task task);
}
