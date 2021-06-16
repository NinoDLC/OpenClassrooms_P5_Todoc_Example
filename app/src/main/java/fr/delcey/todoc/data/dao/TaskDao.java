package fr.delcey.todoc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import fr.delcey.todoc.data.entity.ProjectWithTasksEntity;
import fr.delcey.todoc.data.entity.TaskEntity;

@Dao
public interface TaskDao {

    @Insert
    long insert(TaskEntity tasksEntity);

    @Transaction
    @Query("SELECT * FROM project")
    LiveData<List<ProjectWithTasksEntity>> getAllProjectsWithTasks();

    @Query("DELETE FROM task WHERE id=:taskId")
    int delete(long taskId);

}