package fr.delcey.todoc.ui.add;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import fr.delcey.todoc.utils.Generated;

public class AddTaskViewState {

    @NonNull
    private final List<AddTaskViewStateItem> addTaskViewStateItems;

    private final boolean isProgressBarVisible;

    public AddTaskViewState(@NonNull List<AddTaskViewStateItem> addTaskViewStateItems, boolean isProgressBarVisible) {
        this.addTaskViewStateItems = addTaskViewStateItems;
        this.isProgressBarVisible = isProgressBarVisible;
    }

    @NonNull
    public List<AddTaskViewStateItem> getAddTaskViewStateItems() {
        return addTaskViewStateItems;
    }

    public boolean isProgressBarVisible() {
        return isProgressBarVisible;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddTaskViewState that = (AddTaskViewState) o;
        return isProgressBarVisible == that.isProgressBarVisible &&
            addTaskViewStateItems.equals(that.addTaskViewStateItems);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(addTaskViewStateItems, isProgressBarVisible);
    }

    @NonNull
    @Generated
    @Override
    public String toString() {
        return "AddTaskViewState{" +
            "addTaskViewStateItems=" + addTaskViewStateItems +
            ", isProgressBarVisible=" + isProgressBarVisible +
            '}';
    }
}
