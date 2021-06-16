package fr.delcey.todoc.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.delcey.todoc.R;
import fr.delcey.todoc.ui.add.AddTaskDialogFragment;
import fr.delcey.todoc.ui.task.TaskFragment;

public class MainActivity extends AppCompatActivity implements NavigationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.main_frame_layout, TaskFragment.newInstance())
            .commitNow();
    }

    @Override
    public void displayAddTaskDialog() {
        AddTaskDialogFragment.newInstance().show(getSupportFragmentManager(), null);
    }
}