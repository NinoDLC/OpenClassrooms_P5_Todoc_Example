package fr.delcey.todoc.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.delcey.todoc.data.entity.ProjectEntity;

@Dao
public interface ProjectDao {

    @Insert
    long insert(ProjectEntity projectEntity);

    @Query("SELECT * FROM project")
    LiveData<List<ProjectEntity>> getAll();

    @Query("SELECT * FROM project")
    List<ProjectEntity> getAllSync();
}
