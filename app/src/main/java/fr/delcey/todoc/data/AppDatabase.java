package fr.delcey.todoc.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executor;

import fr.delcey.todoc.BuildConfig;
import fr.delcey.todoc.R;
import fr.delcey.todoc.data.dao.ProjectDao;
import fr.delcey.todoc.data.dao.TaskDao;
import fr.delcey.todoc.data.entity.ProjectEntity;
import fr.delcey.todoc.data.entity.TaskEntity;

@Database(
    entities = {
        TaskEntity.class,
        ProjectEntity.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "app_database";

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(
        @NonNull Application application,
        @NonNull Executor ioExecutor
    ) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = create(application, ioExecutor);
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase create(
        @NonNull Application application,
        @NonNull Executor ioExecutor
    ) {
        Builder<AppDatabase> builder = Room.databaseBuilder(
            application,
            AppDatabase.class,
            DATABASE_NAME
        );

        builder.addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                ioExecutor.execute(() -> {
                    ProjectDao projectDao = AppDatabase.getInstance(application, ioExecutor).getProjectDao();

                    projectDao.insert(
                        new ProjectEntity(
                            application.getString(R.string.tartampion_project),
                            ResourcesCompat.getColor(application.getResources(), R.color.project_color_tartampion, null)
                        )
                    );
                    projectDao.insert(
                        new ProjectEntity(
                            application.getString(R.string.lucidia_project),
                            ResourcesCompat.getColor(application.getResources(), R.color.project_color_lucidia, null)
                        )
                    );
                    projectDao.insert(
                        new ProjectEntity(
                            application.getString(R.string.circus_project),
                            ResourcesCompat.getColor(application.getResources(), R.color.project_color_circus, null)
                        )
                    );
                });
            }
        });

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration();
        }

        return builder.build();
    }

    public abstract TaskDao getTaskDao();

    public abstract ProjectDao getProjectDao();

}
