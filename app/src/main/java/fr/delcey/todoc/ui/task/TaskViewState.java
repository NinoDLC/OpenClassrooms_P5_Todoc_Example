package fr.delcey.todoc.ui.task;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import fr.delcey.todoc.utils.Generated;

public abstract class TaskViewState {

    public enum Type {
        HEADER,
        TASK,
        EMPTY_STATE
    }

    @NonNull
    protected final Type type;

    public TaskViewState(@NonNull Type type) {
        this.type = type;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Override
    public abstract boolean equals(@Nullable Object obj);

    public static class Header extends TaskViewState {

        @NonNull
        private final String title;

        public Header(@NonNull String title) {
            super(Type.HEADER);

            this.title = title;
        }

        @NonNull
        public String getTitle() {
            return title;
        }

        @Generated
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return title.equals(header.title);
        }

        @Generated
        @Override
        public int hashCode() {
            return Objects.hash(title);
        }

        @Generated
        @NonNull
        @Override
        public String toString() {
            return "Header{" +
                "type=" + type +
                ", title='" + title + '\'' +
                '}';
        }
    }

    public static class Task extends TaskViewState {

        private final long taskId;

        @ColorInt
        private final int projectColor;

        @NonNull
        private final String description;

        public Task(long taskId, @ColorInt int projectColor, @NonNull String description) {
            super(Type.TASK);

            this.taskId = taskId;
            this.projectColor = projectColor;
            this.description = description;
        }

        public long getTaskId() {
            return taskId;
        }

        @ColorInt
        public int getProjectColor() {
            return projectColor;
        }

        @NonNull
        public String getDescription() {
            return description;
        }

        @Generated
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Task task = (Task) o;
            return taskId == task.taskId &&
                projectColor == task.projectColor &&
                description.equals(task.description);
        }

        @Generated
        @Override
        public int hashCode() {
            return Objects.hash(taskId, projectColor, description);
        }

        @Generated
        @NonNull
        @Override
        public String toString() {
            return "Task{" +
                "type=" + type +
                ", taskId=" + taskId +
                ", projectColor=" + projectColor +
                ", description='" + description + '\'' +
                '}';
        }
    }

    public static class EmptyState extends TaskViewState {

        public EmptyState() {
            super(Type.EMPTY_STATE);
        }

        @Generated
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o != null && getClass() == o.getClass();
        }

        @Generated
        @NonNull
        @Override
        public String toString() {
            return "EmptyState{" +
                "type=" + type +
                '}';
        }
    }
}
