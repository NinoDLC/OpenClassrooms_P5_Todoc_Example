package fr.delcey.todoc.data.entity;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

import fr.delcey.todoc.utils.Generated;

@Entity(tableName = "project")
public class ProjectEntity {

    @PrimaryKey(autoGenerate = true)
    private final long id;

    @NonNull
    private final String projectName;

    @ColorInt
    private final int colorInt;

    @Ignore
    public ProjectEntity(@NonNull String projectName, @ColorInt int colorInt) {
        this(0, projectName, colorInt);
    }

    /**
     * Don't use this constructor, this is for Room / UTs only. Room ain't so good in Java with immutability.
     */
    @VisibleForTesting
    public ProjectEntity(long id, @NonNull String projectName, @ColorInt int colorInt) {
        this.id = id;
        this.projectName = projectName;
        this.colorInt = colorInt;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getProjectName() {
        return projectName;
    }

    @ColorInt
    public int getColorInt() {
        return colorInt;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectEntity that = (ProjectEntity) o;
        return id == that.id &&
            colorInt == that.colorInt &&
            projectName.equals(that.projectName);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(id, projectName, colorInt);
    }

    @Generated
    @NonNull
    @Override
    public String toString() {
        return "ProjectEntity{" +
            "id=" + id +
            ", projectName='" + projectName + '\'' +
            ", colorInt=" + colorInt +
            '}';
    }
}
