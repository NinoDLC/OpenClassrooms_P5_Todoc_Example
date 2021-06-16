package fr.delcey.todoc.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.delcey.todoc.data.AppDatabase;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.ProjectWithTasksEntity;
import fr.delcey.todoc.data.entity.TaskEntity;
import fr.delcey.todoc.utils.LiveDataTestUtils;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    private static final int EXPECTED_PROJECT_ID_1 = 42;
    private static final String EXPECTED_PROJECT_NAME_1 = "EXPECTED_PROJECT_NAME_1";
    private static final int EXPECTED_PROJECT_COLOR_1 = 1;

    private static final int EXPECTED_PROJECT_ID_2 = 666;
    private static final String EXPECTED_PROJECT_NAME_2 = "EXPECTED_PROJECT_NAME_2";
    private static final int EXPECTED_PROJECT_COLOR_2 = 2;

    private static final int EXPECTED_TASK_PROJECT_ID_1 = EXPECTED_PROJECT_ID_1;
    private static final String EXPECTED_TASK_DESCRIPTION_1 = "EXPECTED_TASK_DESCRIPTION_1";

    private static final int EXPECTED_TASK_PROJECT_ID_2 = EXPECTED_PROJECT_ID_1;
    private static final String EXPECTED_TASK_DESCRIPTION_2 = "EXPECTED_TASK_DESCRIPTION_2";

    private static final int EXPECTED_TASK_PROJECT_ID_3 = EXPECTED_PROJECT_ID_2;
    private static final String EXPECTED_TASK_DESCRIPTION_3 = "EXPECTED_TASK_DESCRIPTION_3";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase appDatabase;
    private TaskDao taskDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        appDatabase = Room
            .inMemoryDatabaseBuilder(context, AppDatabase.class)
            .build();
        taskDao = appDatabase.getTaskDao();

        appDatabase.getProjectDao().insert(getFirstProjectEntity());
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void insert_one() {
        // Given
        TaskEntity taskEntity = new TaskEntity(EXPECTED_TASK_PROJECT_ID_1, EXPECTED_TASK_DESCRIPTION_1);

        // When
        long taskId = taskDao.insert(taskEntity);
        List<ProjectWithTasksEntity> results = LiveDataTestUtils.getValueForTesting(taskDao.getAllProjectsWithTasks());

        // Then
        assertEquals(1, taskId);
        assertEquals(
            Collections.singletonList(
                new ProjectWithTasksEntity(
                    getFirstProjectEntity(),
                    Collections.singletonList(
                        new TaskEntity(
                            1,
                            EXPECTED_PROJECT_ID_1,
                            EXPECTED_TASK_DESCRIPTION_1
                        )
                    )
                )
            ),
            results
        );
    }

    @Test
    public void insert_two() {
        // Given
        TaskEntity taskEntity = new TaskEntity(EXPECTED_TASK_PROJECT_ID_1, EXPECTED_TASK_DESCRIPTION_1);
        TaskEntity taskEntity2 = new TaskEntity(EXPECTED_TASK_PROJECT_ID_2, EXPECTED_TASK_DESCRIPTION_2);

        // When
        long taskId = taskDao.insert(taskEntity);
        long taskId2 = taskDao.insert(taskEntity2);
        List<ProjectWithTasksEntity> results = LiveDataTestUtils.getValueForTesting(taskDao.getAllProjectsWithTasks());

        // Then
        assertEquals(1, taskId);
        assertEquals(2, taskId2);
        assertEquals(
            Collections.singletonList(
                new ProjectWithTasksEntity(
                    getFirstProjectEntity(),
                    Arrays.asList(
                        new TaskEntity(
                            1,
                            EXPECTED_TASK_PROJECT_ID_1,
                            EXPECTED_TASK_DESCRIPTION_1
                        ),
                        new TaskEntity(
                            2,
                            EXPECTED_TASK_PROJECT_ID_2,
                            EXPECTED_TASK_DESCRIPTION_2
                        )
                    )
                )
            ),
            results
        );
    }

    @Test
    public void insert_three_with_2_projects() {
        // Given
        TaskEntity taskEntity = new TaskEntity(EXPECTED_TASK_PROJECT_ID_1, EXPECTED_TASK_DESCRIPTION_1);
        TaskEntity taskEntity2 = new TaskEntity(EXPECTED_TASK_PROJECT_ID_2, EXPECTED_TASK_DESCRIPTION_2);
        TaskEntity taskEntity3 = new TaskEntity(EXPECTED_TASK_PROJECT_ID_3, EXPECTED_TASK_DESCRIPTION_3);

        // When
        appDatabase.getProjectDao().insert(getSecondProjectEntity());
        long taskId = taskDao.insert(taskEntity);
        long taskId2 = taskDao.insert(taskEntity2);
        long taskId3 = taskDao.insert(taskEntity3);
        List<ProjectWithTasksEntity> results = LiveDataTestUtils.getValueForTesting(taskDao.getAllProjectsWithTasks());

        // Then
        assertEquals(1, taskId);
        assertEquals(2, taskId2);
        assertEquals(3, taskId3);
        assertEquals(
            Arrays.asList(
                new ProjectWithTasksEntity(
                    getFirstProjectEntity(),
                    Arrays.asList(
                        new TaskEntity(
                            1,
                            EXPECTED_TASK_PROJECT_ID_1,
                            EXPECTED_TASK_DESCRIPTION_1
                        ),
                        new TaskEntity(
                            2,
                            EXPECTED_TASK_PROJECT_ID_2,
                            EXPECTED_TASK_DESCRIPTION_2
                        )
                    )
                ),
                new ProjectWithTasksEntity(
                    getSecondProjectEntity(),
                    Collections.singletonList(
                        new TaskEntity(
                            3,
                            EXPECTED_TASK_PROJECT_ID_3,
                            EXPECTED_TASK_DESCRIPTION_3
                        )
                    )
                )
            ),
            results
        );
    }

    @Test(expected = SQLiteException.class)
    public void should_throw_with_incorrect_project_foreign_key() {
        TaskEntity taskEntity = new TaskEntity(4, EXPECTED_TASK_DESCRIPTION_1);

        taskDao.insert(taskEntity);
    }

    @Test
    public void insert_and_delete() {
        // Given
        TaskEntity taskEntity = new TaskEntity(EXPECTED_TASK_PROJECT_ID_1, EXPECTED_TASK_DESCRIPTION_1);

        // When
        taskDao.insert(taskEntity);
        taskDao.delete(1);
        List<ProjectWithTasksEntity> results = LiveDataTestUtils.getValueForTesting(taskDao.getAllProjectsWithTasks());

        // Then
        assertEquals(
            Collections.singletonList(
                new ProjectWithTasksEntity(
                    getFirstProjectEntity(),
                    new ArrayList<>()
                )
            ),
            results
        );
    }

    @Test
    public void insert_two_and_delete_one() {
        // Given
        TaskEntity taskEntity = new TaskEntity(EXPECTED_TASK_PROJECT_ID_1, EXPECTED_TASK_DESCRIPTION_1);
        TaskEntity taskEntity2 = new TaskEntity(EXPECTED_TASK_PROJECT_ID_2, EXPECTED_TASK_DESCRIPTION_2);

        // When
        taskDao.insert(taskEntity);
        taskDao.insert(taskEntity2);
        taskDao.delete(1);
        List<ProjectWithTasksEntity> results = LiveDataTestUtils.getValueForTesting(taskDao.getAllProjectsWithTasks());

        // Then
        assertEquals(
            Collections.singletonList(
                new ProjectWithTasksEntity(
                    getFirstProjectEntity(),
                    Collections.singletonList(
                        new TaskEntity(
                            2,
                            EXPECTED_TASK_PROJECT_ID_2,
                            EXPECTED_TASK_DESCRIPTION_2
                        )
                    )
                )
            ),
            results
        );
    }

    @Test
    public void should_return_0_when_nothing_is_deleted() {
        // When
        int deletedCount = taskDao.delete(1);

        // Then
        assertEquals(0, deletedCount);
    }

    // region IN
    private ProjectEntity getFirstProjectEntity() {
        return new ProjectEntity(EXPECTED_PROJECT_ID_1, EXPECTED_PROJECT_NAME_1, EXPECTED_PROJECT_COLOR_1);
    }

    private ProjectEntity getSecondProjectEntity() {
        return new ProjectEntity(EXPECTED_PROJECT_ID_2, EXPECTED_PROJECT_NAME_2, EXPECTED_PROJECT_COLOR_2);
    }
    // endregion IN
}