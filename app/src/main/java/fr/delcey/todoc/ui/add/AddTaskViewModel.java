package fr.delcey.todoc.ui.add;

import android.app.Application;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import fr.delcey.todoc.R;
import fr.delcey.todoc.data.BuildConfigResolver;
import fr.delcey.todoc.data.TaskRepository;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.TaskEntity;
import fr.delcey.todoc.utils.EmptySingleLiveEvent;
import fr.delcey.todoc.utils.SingleLiveEvent;

public class AddTaskViewModel extends ViewModel {

    @NonNull
    private final Application application;
    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final BuildConfigResolver buildConfigResolver;
    @NonNull
    private final Executor mainExecutor;
    @NonNull
    private final Executor ioExecutor;

    private final MediatorLiveData<AddTaskViewState> addTaskViewStateMediatorLiveData = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> isAddingTaskInDatabaseMutableLiveData = new MutableLiveData<>();

    private final SingleLiveEvent<String> displayToastMessageSingleLiveEvent = new SingleLiveEvent<>();

    private final EmptySingleLiveEvent dismissDialogSingleLiveEvent = new EmptySingleLiveEvent();

    @Nullable
    private Long projectId;
    @Nullable
    private String taskDescription;

    public AddTaskViewModel(
        @NonNull Application application,
        @NonNull TaskRepository taskRepository,
        @NonNull BuildConfigResolver buildConfigResolver,
        @NonNull Executor mainExecutor,
        @NonNull Executor ioExecutor
    ) {
        this.application = application;
        this.taskRepository = taskRepository;
        this.buildConfigResolver = buildConfigResolver;
        this.mainExecutor = mainExecutor;
        this.ioExecutor = ioExecutor;

        LiveData<List<ProjectEntity>> allProjectsLiveData = taskRepository.getAllProjects();

        addTaskViewStateMediatorLiveData.addSource(allProjectsLiveData, projectEntities ->
            combine(projectEntities, isAddingTaskInDatabaseMutableLiveData.getValue())
        );

        addTaskViewStateMediatorLiveData.addSource(isAddingTaskInDatabaseMutableLiveData, isAddingTaskInDatabase ->
            combine(allProjectsLiveData.getValue(), isAddingTaskInDatabase)
        );
    }

    private void combine(@Nullable List<ProjectEntity> projectEntities, @Nullable Boolean isAddingTaskInDatabase) {
        if (projectEntities == null) {
            return;
        }

        List<AddTaskViewStateItem> addTaskViewStateItems = new ArrayList<>();

        for (ProjectEntity projectEntity : projectEntities) {
            addTaskViewStateItems.add(
                new AddTaskViewStateItem(
                    projectEntity.getId(),
                    projectEntity.getColorInt(),
                    projectEntity.getProjectName()
                )
            );
        }

        addTaskViewStateMediatorLiveData.setValue(
            new AddTaskViewState(
                addTaskViewStateItems,
                isAddingTaskInDatabase != null && isAddingTaskInDatabase
            )
        );
    }

    public LiveData<AddTaskViewState> getAddTaskViewStateLiveData() {
        return addTaskViewStateMediatorLiveData;
    }

    public SingleLiveEvent<String> getDisplayToastMessageSingleLiveEvent() {
        return displayToastMessageSingleLiveEvent;
    }

    public EmptySingleLiveEvent getDismissDialogSingleLiveEvent() {
        return dismissDialogSingleLiveEvent;
    }

    public void onProjectSelected(long projectId) {
        this.projectId = projectId;
    }

    public void onTaskDescriptionChanged(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void onOkButtonClicked() {
        if (projectId != null && taskDescription != null && !taskDescription.isEmpty()) {
            isAddingTaskInDatabaseMutableLiveData.setValue(true);

            ioExecutor.execute(() -> {

                try {
                    taskRepository.addTask(new TaskEntity(projectId, taskDescription));

                    mainExecutor.execute(() -> dismissDialogSingleLiveEvent.call());
                } catch (SQLiteException e) {
                    if (buildConfigResolver.isDebug()) {
                        e.printStackTrace();
                    }

                    mainExecutor.execute(() ->
                        displayToastMessageSingleLiveEvent.setValue(application.getString(R.string.cant_insert_task))
                    );
                }

                mainExecutor.execute(() -> isAddingTaskInDatabaseMutableLiveData.setValue(false));
            });
        }
    }
}
