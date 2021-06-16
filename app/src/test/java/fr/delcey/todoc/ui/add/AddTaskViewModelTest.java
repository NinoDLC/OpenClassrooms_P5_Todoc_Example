package fr.delcey.todoc.ui.add;

import android.app.Application;
import android.database.sqlite.SQLiteException;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import fr.delcey.todoc.R;
import fr.delcey.todoc.data.BuildConfigResolver;
import fr.delcey.todoc.data.TaskRepository;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.TaskEntity;
import fr.delcey.todoc.utils.LiveDataTestUtils;
import fr.delcey.todoc.utils.TestExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class AddTaskViewModelTest {

    private static final String EXPECTED_PROJECT_NAME = "projectName";
    private static final int EXPECTED_PROJECT_COUNT = 3;
    private static final String EXPECTED_CANT_INSERT_TASK = "cant_insert_task";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final Application application = Mockito.mock(Application.class);
    private final TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
    private final BuildConfigResolver buildConfigResolver = Mockito.mock(BuildConfigResolver.class);
    private final Executor ioExecutor = Mockito.spy(new TestExecutor());
    private final Executor mainExecutor = Mockito.spy(new TestExecutor());

    private final MutableLiveData<List<ProjectEntity>> projectsMutableLiveData = new MutableLiveData<>();

    private AddTaskViewModel viewModel;

    @Before
    public void setUp() {
        Mockito.doReturn(projectsMutableLiveData).when(taskRepository).getAllProjects();

        projectsMutableLiveData.setValue(getDefaultProjectEntities());

        viewModel = new AddTaskViewModel(application, taskRepository, buildConfigResolver, mainExecutor, ioExecutor);

        Mockito.verify(taskRepository).getAllProjects();
    }

    @Test
    public void nominal_case() {
        // When
        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void empty_projects() {
        // Given
        projectsMutableLiveData.setValue(new ArrayList<>());

        // When
        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(new ArrayList<>(), false), addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
    }

    @Test
    public void null_projects() {
        // Given
        projectsMutableLiveData.setValue(null);

        // When
        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertNull(addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
    }

    @Test
    public void add_task_successfully() {
        // Given
        long selectedProjectId = 1;
        String taskDescription = "taskDescription";

        // When
        viewModel.onProjectSelected(selectedProjectId);
        viewModel.onTaskDescriptionChanged(taskDescription);
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertNull(toastMessage);
        assertTrue(dismissDialog);
        Mockito.verify(ioExecutor).execute(any());
        Mockito.verify(taskRepository).addTask(new TaskEntity(selectedProjectId, taskDescription));
        Mockito.verify(mainExecutor, Mockito.times(2)).execute(any());
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void add_task_failed_because_no_selected_project() {
        // Given
        String taskDescription = "taskDescription";

        // When
        viewModel.onTaskDescriptionChanged(taskDescription);
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void add_task_failed_because_no_description() {
        // Given
        long selectedProjectId = 1;

        // When
        viewModel.onProjectSelected(selectedProjectId);
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void add_task_failed_because_empty_description() {
        // Given
        long selectedProjectId = 1;

        // When
        viewModel.onProjectSelected(selectedProjectId);
        viewModel.onTaskDescriptionChanged("");
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void add_task_failed_because_no_info() {
        // When
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertNull(toastMessage);
        assertNull(dismissDialog);
        Mockito.verifyNoMoreInteractions(taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void add_task_failed_because_SQL_exception_in_debug() {
        // Given
        SQLiteException exception = Mockito.mock(SQLiteException.class);
        Mockito.doThrow(exception).when(taskRepository).addTask(any());
        Mockito.doReturn(EXPECTED_CANT_INSERT_TASK).when(application).getString(R.string.cant_insert_task);
        Mockito.doReturn(true).when(buildConfigResolver).isDebug();

        // When
        viewModel.onProjectSelected(1);
        viewModel.onTaskDescriptionChanged("taskDescription");
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertEquals(EXPECTED_CANT_INSERT_TASK, toastMessage);
        assertNull(dismissDialog);
        Mockito.verify(ioExecutor).execute(any());
        Mockito.verify(taskRepository).addTask(any());
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verify(exception).printStackTrace();
        Mockito.verify(mainExecutor, Mockito.times(2)).execute(any());
        Mockito.verifyNoMoreInteractions(exception, taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    @Test
    public void add_task_failed_because_SQL_exception_in_release() {
        // Given
        SQLiteException exception = Mockito.mock(SQLiteException.class);
        Mockito.doThrow(exception).when(taskRepository).addTask(any());
        Mockito.doReturn(EXPECTED_CANT_INSERT_TASK).when(application).getString(R.string.cant_insert_task);
        Mockito.doReturn(false).when(buildConfigResolver).isDebug();

        // When
        viewModel.onProjectSelected(1);
        viewModel.onTaskDescriptionChanged("taskDescription");
        viewModel.onOkButtonClicked();

        AddTaskViewState addTaskViewState = LiveDataTestUtils.getValueForTesting(viewModel.getAddTaskViewStateLiveData());
        String toastMessage = LiveDataTestUtils.getValueForTesting(viewModel.getDisplayToastMessageSingleLiveEvent());
        Boolean dismissDialog = LiveDataTestUtils.getValueForTesting(viewModel.getDismissDialogSingleLiveEvent());

        // Then
        assertEquals(new AddTaskViewState(getDefaultAddTaskViewStateItems(), false), addTaskViewState);
        assertEquals(EXPECTED_CANT_INSERT_TASK, toastMessage);
        assertNull(dismissDialog);
        Mockito.verify(ioExecutor).execute(any());
        Mockito.verify(taskRepository).addTask(any());
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verify(mainExecutor, Mockito.times(2)).execute(any());
        Mockito.verifyNoMoreInteractions(exception, taskRepository, buildConfigResolver, mainExecutor, ioExecutor);
    }

    // region IN
    @NonNull
    private List<ProjectEntity> getDefaultProjectEntities() {
        List<ProjectEntity> projectEntities = new ArrayList<>();

        for (int i = 0; i < EXPECTED_PROJECT_COUNT; i++) {
            projectEntities.add(new ProjectEntity(i, EXPECTED_PROJECT_NAME + i, i));
        }

        return projectEntities;
    }
    // endregion IN

    // region OUT
    @NonNull
    private List<AddTaskViewStateItem> getDefaultAddTaskViewStateItems() {

        List<AddTaskViewStateItem> items = new ArrayList<>();

        for (int i = 0; i < EXPECTED_PROJECT_COUNT; i++) {
            items.add(new AddTaskViewStateItem(i, i, EXPECTED_PROJECT_NAME + i));
        }
        return items;
    }
    // endregion OUT

}