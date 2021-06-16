package fr.delcey.todoc.data;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Random;

import fr.delcey.todoc.Mock;
import fr.delcey.todoc.data.dao.ProjectDao;
import fr.delcey.todoc.data.dao.TaskDao;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.ProjectWithTasksEntity;
import fr.delcey.todoc.data.entity.TaskEntity;
import fr.delcey.todoc.utils.ThreadSleeper;

public class TaskRepository {

    private static final int DEBUG_MOCK_INSERT_DURATION = 1_200;

    @NonNull
    private final TaskDao taskDao;
    @NonNull
    private final ProjectDao projectDao;
    @NonNull
    private final BuildConfigResolver buildConfigResolver;
    @NonNull
    private final ThreadSleeper threadSleeper;

    public TaskRepository(
        @NonNull TaskDao taskDao,
        @NonNull ProjectDao projectDao,
        @NonNull BuildConfigResolver buildConfigResolver,
        @NonNull ThreadSleeper threadSleeper
    ) {
        this.taskDao = taskDao;
        this.projectDao = projectDao;
        this.buildConfigResolver = buildConfigResolver;
        this.threadSleeper = threadSleeper;
    }

    @MainThread
    public LiveData<List<ProjectEntity>> getAllProjects() {
        return projectDao.getAll();
    }

    @MainThread
    public LiveData<List<ProjectWithTasksEntity>> getAllProjectsWithTasks() {
        return taskDao.getAllProjectsWithTasks();
    }

    @WorkerThread
    public void addTask(@NonNull TaskEntity taskEntity) {

        if (buildConfigResolver.isDebug()) {
            // Make it look like a long operation
            try {
                threadSleeper.sleep(DEBUG_MOCK_INSERT_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        taskDao.insert(taskEntity);
    }

    @WorkerThread
    public void addRandomTask() {
        if (buildConfigResolver.isDebug()) {
            List<ProjectEntity> projects = projectDao.getAllSync();

            ProjectEntity randomlySelectedProject = projects.get(new Random().nextInt(projects.size()));

            taskDao.insert(
                new TaskEntity(
                    randomlySelectedProject.getId(),
                    Mock.getRandomTaskDescription()
                )
            );
        }
    }

    @WorkerThread
    public void delete(long taskId) {
        taskDao.delete(taskId);
    }
}
