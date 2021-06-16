package fr.delcey.todoc.ui.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import fr.delcey.todoc.data.BuildConfigResolver;
import fr.delcey.todoc.data.TaskRepository;
import fr.delcey.todoc.data.entity.ProjectWithTasksEntity;
import fr.delcey.todoc.data.entity.TaskEntity;

public class TaskViewModel extends ViewModel {

    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final BuildConfigResolver buildConfigResolver;
    @NonNull
    private final Executor ioExecutor;

    private final MediatorLiveData<List<TaskViewState>> viewStateMediatorLiveData = new MediatorLiveData<>();

    private final MutableLiveData<TaskSortingType> taskSortingTypeMutableLiveData = new MutableLiveData<>();

    public TaskViewModel(
        @NonNull TaskRepository taskRepository,
        @NonNull BuildConfigResolver buildConfigResolver,
        @NonNull Executor ioExecutor
    ) {
        this.taskRepository = taskRepository;
        this.buildConfigResolver = buildConfigResolver;
        this.ioExecutor = ioExecutor;

        LiveData<List<ProjectWithTasksEntity>> projectsWithTasksLiveData = taskRepository.getAllProjectsWithTasks();

        viewStateMediatorLiveData.addSource(projectsWithTasksLiveData, projectWithTasks ->
            combine(projectWithTasks, taskSortingTypeMutableLiveData.getValue())
        );

        viewStateMediatorLiveData.addSource(taskSortingTypeMutableLiveData, taskSortingType ->
            combine(projectsWithTasksLiveData.getValue(), taskSortingType)
        );
    }

    private void combine(@Nullable List<ProjectWithTasksEntity> projectsWithTasks, @Nullable TaskSortingType taskSortingType) {
        if (projectsWithTasks == null) {
            return;
        }

        List<TaskViewState> taskViewStates = new ArrayList<>();

        if (taskSortingType == TaskSortingType.PROJECT_ALPHABETICAL) {
            for (ProjectWithTasksEntity projectWithTasksEntity : projectsWithTasks) {
                boolean addedHeader = false;

                for (TaskEntity taskEntity : projectWithTasksEntity.getTaskEntities()) {
                    if (!addedHeader) {
                        addedHeader = true;
                        taskViewStates.add(new TaskViewState.Header(projectWithTasksEntity.getProjectEntity().getProjectName()));
                    }
                    taskViewStates.add(mapItem(projectWithTasksEntity, taskEntity));
                }
            }
        } else {
            Set<TaskViewState.Task> set = new TreeSet<>((o1, o2) -> Long.compare(o1.getTaskId(), o2.getTaskId()));
            for (ProjectWithTasksEntity projectWithTask : projectsWithTasks) {
                for (TaskEntity taskEntity : projectWithTask.getTaskEntities()) {
                    set.add(mapItem(projectWithTask, taskEntity));
                }
            }

            taskViewStates.addAll(set);
        }

        if (taskViewStates.isEmpty()) {
            taskViewStates.add(new TaskViewState.EmptyState());
        }

        viewStateMediatorLiveData.setValue(taskViewStates);
    }

    private TaskViewState.Task mapItem(ProjectWithTasksEntity projectWithTask, TaskEntity taskEntity) {
        return new TaskViewState.Task(
            taskEntity.getId(),
            projectWithTask.getProjectEntity().getColorInt(),
            taskEntity.getTaskDescription()
        );
    }

    public void onSortButtonClicked() {
        TaskSortingType oldValue = taskSortingTypeMutableLiveData.getValue();
        TaskSortingType newValue;

        if (oldValue == null) {
            newValue = TaskSortingType.PROJECT_ALPHABETICAL;
        } else {
            newValue = null;
        }

        taskSortingTypeMutableLiveData.setValue(newValue);
    }

    public void onDeleteTaskButtonClicked(long taskId) {
        ioExecutor.execute(() -> taskRepository.delete(taskId));
    }

    @NonNull
    public LiveData<List<TaskViewState>> getViewStateLiveData() {
        return viewStateMediatorLiveData;
    }

    public void onAddButtonLongClicked() {
        if (buildConfigResolver.isDebug()) {
            ioExecutor.execute(() -> taskRepository.addRandomTask());
        }
    }
}