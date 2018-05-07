package database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import model.Task;

@Database(entities = {Task.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    private static AppDatabase INSTANCE;

    public abstract TaskDao taskDao();

    public static synchronized AppDatabase getAppDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context,AppDatabase.class,"application-database")
                    .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
