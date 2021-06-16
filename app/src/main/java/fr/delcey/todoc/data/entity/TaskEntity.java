package fr.delcey.todoc.data.entity;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

import fr.delcey.todoc.utils.Generated;

@Entity(
    tableName = "task",
    foreignKeys = @ForeignKey(
        entity = ProjectEntity.class,
        parentColumns = "id",
        childColumns = "projectId"
    )
)
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    private final long id;

    @ColumnInfo(index = true)
    private final long projectId;

    @NonNull
    private final String taskDescription;

    @Ignore
    public TaskEntity(long projectId, @NonNull String taskDescription) {
        this(0, projectId, taskDescription);
    }

    /**
     * Don't use this constructor, this is for Room only. Room ain't so good in Java with immutability.
     */
    @VisibleForTesting
    public TaskEntity(long id, long projectId, @NonNull String taskDescription) {
        this.id = id;
        this.projectId = projectId;
        this.taskDescription = taskDescription;
    }

    public long getId() {
        return id;
    }

    public long getProjectId() {
        return projectId;
    }

    @NonNull
    public String getTaskDescription() {
        return taskDescription;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return id == that.id &&
            projectId == that.projectId &&
            Objects.equals(taskDescription, that.taskDescription);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, taskDescription);
    }

    @Generated
    @NonNull
    @Override
    public String toString() {
        return "TaskEntity{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", taskDescription='" + taskDescription + '\'' +
            '}';
    }
}
