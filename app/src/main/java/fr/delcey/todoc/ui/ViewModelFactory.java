package fr.delcey.todoc.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.delcey.todoc.MainApplication;
import fr.delcey.todoc.data.AppDatabase;
import fr.delcey.todoc.data.BuildConfigResolver;
import fr.delcey.todoc.data.TaskRepository;
import fr.delcey.todoc.ui.add.AddTaskViewModel;
import fr.delcey.todoc.ui.task.TaskViewModel;
import fr.delcey.todoc.utils.MainThreadExecutor;
import fr.delcey.todoc.utils.ThreadSleeper;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    private final Executor mainThreadExecutor = new MainThreadExecutor();
    private final Executor ioExecutor = Executors.newFixedThreadPool(4);

    private final TaskRepository taskRepository;

    private final BuildConfigResolver buildConfigResolver = new BuildConfigResolver();

    private ViewModelFactory() {
        AppDatabase appDatabase = AppDatabase.getInstance(MainApplication.getApplication(), ioExecutor);

        taskRepository = new TaskRepository(
            appDatabase.getTaskDao(),
            appDatabase.getProjectDao(),
            buildConfigResolver,
            new ThreadSleeper()
        );
    }

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(taskRepository, buildConfigResolver, ioExecutor);
        } else if (modelClass.isAssignableFrom(AddTaskViewModel.class)) {
            return (T) new AddTaskViewModel(
                MainApplication.getApplication(),
                taskRepository,
                buildConfigResolver,
                mainThreadExecutor,
                ioExecutor
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

