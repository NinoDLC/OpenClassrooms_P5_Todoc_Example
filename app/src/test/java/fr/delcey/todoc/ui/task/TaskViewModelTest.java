package fr.delcey.todoc.ui.task;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import fr.delcey.todoc.data.BuildConfigResolver;
import fr.delcey.todoc.data.TaskRepository;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.ProjectWithTasksEntity;
import fr.delcey.todoc.data.entity.TaskEntity;
import fr.delcey.todoc.utils.LiveDataTestUtils;
import fr.delcey.todoc.utils.TestExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

public class TaskViewModelTest {

    private static final String EXPECTED_PROJECT_NAME = "projectName";
    private static final int EXPECTED_PROJECT_COUNT = 3;
    private static final String EXPECTED_TASK_DESCRIPTION = "taskDescription";
    private static final int EXPECTED_TASK_COUNT = 3;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
    private final BuildConfigResolver buildConfigResolver = Mockito.mock(BuildConfigResolver.class);
    private final Executor ioExecutor = Mockito.spy(new TestExecutor());

    private final MutableLiveData<List<ProjectWithTasksEntity>> projectsWithTasksMutableLiveData = new MutableLiveData<>();

    private TaskViewModel viewModel;

    @Before
    public void setUp() {
        Mockito.doReturn(projectsWithTasksMutableLiveData).when(taskRepository).getAllProjectsWithTasks();

        projectsWithTasksMutableLiveData.setValue(getDefaultProjectsWithTasks());

        viewModel = new TaskViewModel(taskRepository, buildConfigResolver, ioExecutor);

        Mockito.verify(taskRepository).getAllProjectsWithTasks();
    }

    @Test
    public void nominal_case() {
        // When
        List<TaskViewState> taskViewStates = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(getDefaultTaskViewStates(), taskViewStates);

        Mockito.verify(ioExecutor, Mockito.never()).execute(any());
        Mockito.verifyNoMoreInteractions(taskRepository, ioExecutor);
    }

    @Test
    public void initial_case() {
        // Given
        projectsWithTasksMutableLiveData.setValue(new ArrayList<>());

        // When
        List<TaskViewState> taskViewStates = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(Collections.singletonList(new TaskViewState.EmptyState()), taskViewStates);
    }

    @Test
    public void sortByProjectAlphabetical() {
        // Given
        viewModel.onSortButtonClicked();

        // When
        List<TaskViewState> taskViewStates = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(getDefaultTaskViewStatesForSortingProjectAlphabetical(), taskViewStates);
    }

    @Test
    public void sortByProjectAlphabetical_twice_is_nominal_case() {
        // Given
        viewModel.onSortButtonClicked();
        viewModel.onSortButtonClicked();

        // When
        List<TaskViewState> taskViewStates = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(getDefaultTaskViewStates(), taskViewStates);
    }

    @Test
    public void sortByProjectAlphabetical_before_data_is_available() {
        // Given
        projectsWithTasksMutableLiveData.setValue(null);
        viewModel.onSortButtonClicked();

        // When
        List<TaskViewState> taskViewStates = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertNull(taskViewStates);
    }

    @Test
    public void sortByProjectAlphabetical_without_data() {
        // Given
        projectsWithTasksMutableLiveData.setValue(new ArrayList<>());
        viewModel.onSortButtonClicked();

        // When
        List<TaskViewState> taskViewStates = LiveDataTestUtils.getValueForTesting(viewModel.getViewStateLiveData());

        // Then
        assertEquals(Collections.singletonList(new TaskViewState.EmptyState()), taskViewStates);
    }

    @Test
    public void verifyOnDeleteTaskButtonClicked() {
        // Given
        long taskId = 666;

        // When
        viewModel.onDeleteTaskButtonClicked(taskId);

        // Then
        Mockito.verify(taskRepository).delete(taskId);
        Mockito.verify(ioExecutor).execute(any());
        Mockito.verifyNoMoreInteractions(taskRepository, ioExecutor);
    }

    @Test
    public void onAddButtonLongPressed_on_debug() {
        // Given
        Mockito.doReturn(true).when(buildConfigResolver).isDebug();

        // When
        viewModel.onAddButtonLongClicked();

        // Then
        Mockito.verify(taskRepository).addRandomTask();
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verify(ioExecutor).execute(any());
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, ioExecutor);
    }

    @Test
    public void onAddButtonLongPressed_on_release() {
        // Given
        Mockito.doReturn(false).when(buildConfigResolver).isDebug();

        // When
        viewModel.onAddButtonLongClicked();

        // Then
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, ioExecutor);
    }

    // region IN
    @NonNull
    private List<ProjectWithTasksEntity> getDefaultProjectsWithTasks() {
        List<ProjectWithTasksEntity> projectsWithTasks = new ArrayList<>();

        int taskId = 0;

        for (int i = 0; i < EXPECTED_PROJECT_COUNT; i++) {
            ProjectEntity projectEntity = new ProjectEntity(i, EXPECTED_PROJECT_NAME + i, i);
            List<TaskEntity> taskEntities = new ArrayList<>();

            for (int j = 0; j < EXPECTED_TASK_COUNT; j++) {
                taskId++;

                taskEntities.add(
                    new TaskEntity(
                        taskId,
                        i,
                        EXPECTED_TASK_DESCRIPTION + taskId
                    )
                );
            }

            projectsWithTasks.add(
                new ProjectWithTasksEntity(
                    projectEntity,
                    taskEntities
                )
            );
        }

        return projectsWithTasks;
    }
    // endregion IN

    // region OUT
    @NonNull
    private List<TaskViewState> getDefaultTaskViewStates() {
        List<TaskViewState> taskViewStates = new ArrayList<>();

        int taskId = 0;

        for (int i = 0; i < EXPECTED_PROJECT_COUNT; i++) {
            for (int j = 0; j < EXPECTED_TASK_COUNT; j++) {
                taskId++;

                taskViewStates.add(
                    new TaskViewState.Task(
                        taskId,
                        i,
                        EXPECTED_TASK_DESCRIPTION + taskId
                    )
                );
            }
        }

        return taskViewStates;
    }

    @NonNull
    private List<TaskViewState> getDefaultTaskViewStatesForSortingProjectAlphabetical() {
        List<TaskViewState> taskViewStates = new ArrayList<>();

        int taskId = 0;

        for (int i = 0; i < EXPECTED_PROJECT_COUNT; i++) {

            taskViewStates.add(
                new TaskViewState.Header(
                    EXPECTED_PROJECT_NAME + i
                )
            );

            for (int j = 0; j < EXPECTED_TASK_COUNT; j++) {
                taskId++;

                taskViewStates.add(
                    new TaskViewState.Task(
                        taskId,
                        i,
                        EXPECTED_TASK_DESCRIPTION + taskId
                    )
                );
            }
        }

        return taskViewStates;
    }
    // endregion OUT
}