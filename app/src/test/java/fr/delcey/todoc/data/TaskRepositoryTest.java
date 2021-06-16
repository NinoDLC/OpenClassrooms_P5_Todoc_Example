package fr.delcey.todoc.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import fr.delcey.todoc.data.dao.ProjectDao;
import fr.delcey.todoc.data.dao.TaskDao;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.ProjectWithTasksEntity;
import fr.delcey.todoc.data.entity.TaskEntity;
import fr.delcey.todoc.utils.ThreadSleeper;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class TaskRepositoryTest {

    private final TaskDao taskDao = Mockito.mock(TaskDao.class);
    private final ProjectDao projectDao = Mockito.mock(ProjectDao.class);
    private final BuildConfigResolver buildConfigResolver = Mockito.mock(BuildConfigResolver.class);
    private final ThreadSleeper threadSleeper = Mockito.mock(ThreadSleeper.class);

    private TaskRepository taskRepository;

    @Before
    public void setUp() {
        taskRepository = new TaskRepository(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_getAllProjects() {
        // Given
        // A mock would be enough here, but since Java is terrible to work with generics, let's use 'spy' instead!
        LiveData<List<ProjectEntity>> projectEntitiesLiveData = Mockito.spy(new MutableLiveData<>());
        Mockito.doReturn(projectEntitiesLiveData).when(projectDao).getAll();

        // When
        LiveData<List<ProjectEntity>> result = taskRepository.getAllProjects();

        // Then
        assertEquals(projectEntitiesLiveData, result);
        Mockito.verify(projectDao).getAll();
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_getAllProjectsWithTasks() {
        // Given
        // A mock would be enough here, but since Java is terrible to work with generics, let's use 'spy' instead!
        LiveData<List<ProjectWithTasksEntity>> projectsWithTasksLiveData = Mockito.spy(new MutableLiveData<>());
        Mockito.doReturn(projectsWithTasksLiveData).when(taskDao).getAllProjectsWithTasks();

        // When
        LiveData<List<ProjectWithTasksEntity>> result = taskRepository.getAllProjectsWithTasks();

        // Then
        assertEquals(projectsWithTasksLiveData, result);
        Mockito.verify(taskDao).getAllProjectsWithTasks();
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_addTask_on_debug() throws InterruptedException {
        // Given
        TaskEntity taskEntity = Mockito.mock(TaskEntity.class);
        Mockito.doReturn(true).when(buildConfigResolver).isDebug();

        // When
        taskRepository.addTask(taskEntity);

        // Then
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verify(threadSleeper).sleep(1_200);
        Mockito.verify(taskDao).insert(taskEntity);
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_addTask_on_release() {
        // Given
        TaskEntity taskEntity = Mockito.mock(TaskEntity.class);
        Mockito.doReturn(false).when(buildConfigResolver).isDebug();

        // When
        taskRepository.addTask(taskEntity);

        // Then
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verify(taskDao).insert(taskEntity);
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_addRandomTask_on_debug() {
        // Given
        List<ProjectEntity> projectEntities = new ArrayList<>();
        projectEntities.add(new ProjectEntity("projectName", 0));
        Mockito.doReturn(projectEntities).when(projectDao).getAllSync();
        Mockito.doReturn(true).when(buildConfigResolver).isDebug();

        // When
        taskRepository.addRandomTask();

        // Then
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verify(projectDao).getAllSync();
        Mockito.verify(taskDao).insert(any());
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_addRandomTask_on_release() {
        // Given
        Mockito.doReturn(false).when(buildConfigResolver).isDebug();

        // When
        taskRepository.addRandomTask();

        // Then
        Mockito.verify(buildConfigResolver).isDebug();
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }

    @Test
    public void verify_delete() {
        // Given
        long taskId = 666;

        // When
        taskRepository.delete(taskId);

        // Then
        Mockito.verify(taskDao).delete(taskId);
        Mockito.verifyNoMoreInteractions(taskDao, projectDao, buildConfigResolver, threadSleeper);
    }
}