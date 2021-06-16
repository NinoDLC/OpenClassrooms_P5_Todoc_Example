package fr.delcey.todoc.ui.add;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.Objects;

import fr.delcey.todoc.utils.Generated;

public class AddTaskViewStateItem {

    private final long projectId;

    @ColorInt
    private final int projectColor;

    @NonNull
    private final String projectName;

    public AddTaskViewStateItem(long projectId, @ColorInt int projectColor, @NonNull String projectName) {
        this.projectId = projectId;
        this.projectColor = projectColor;
        this.projectName = projectName;
    }

    public long getProjectId() {
        return projectId;
    }

    @ColorInt
    public int getProjectColor() {
        return projectColor;
    }

    @NonNull
    public String getProjectName() {
        return projectName;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddTaskViewStateItem that = (AddTaskViewStateItem) o;
        return projectId == that.projectId &&
            projectColor == that.projectColor &&
            projectName.equals(that.projectName);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(projectId, projectColor, projectName);
    }

    @Generated
    @NonNull
    @Override
    public String toString() {
        return "AddTaskViewStateItem{" +
            "projectId=" + projectId +
            ", projectColor=" + projectColor +
            ", projectName='" + projectName + '\'' +
            '}';
    }
}
