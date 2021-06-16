package fr.delcey.todoc.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;
import java.util.Objects;

import fr.delcey.todoc.utils.Generated;

public class ProjectWithTasksEntity {
    @NonNull
    @Embedded
    private final ProjectEntity projectEntity;

    @NonNull
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    private final List<TaskEntity> taskEntities;

    public ProjectWithTasksEntity(@NonNull ProjectEntity projectEntity, @NonNull List<TaskEntity> taskEntities) {
        this.projectEntity = projectEntity;
        this.taskEntities = taskEntities;
    }

    @NonNull
    public ProjectEntity getProjectEntity() {
        return projectEntity;
    }

    @NonNull
    public List<TaskEntity> getTaskEntities() {
        return taskEntities;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectWithTasksEntity that = (ProjectWithTasksEntity) o;
        return Objects.equals(projectEntity, that.projectEntity) &&
            Objects.equals(taskEntities, that.taskEntities);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(projectEntity, taskEntities);
    }

    @Generated
    @NonNull
    @Override
    public String toString() {
        return "ProjectWithTasks{" +
            "projectEntity=" + projectEntity +
            ", taskEntities=" + taskEntities +
            '}';
    }
}
