package fr.delcey.todoc.ui.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.delcey.todoc.R;
import fr.delcey.todoc.ui.NavigationListener;
import fr.delcey.todoc.ui.ViewModelFactory;
import fr.delcey.todoc.databinding.TaskFragmentBinding;

public class TaskFragment extends Fragment {

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    private NavigationListener navigationListener;

    private TaskViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        navigationListener = (NavigationListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        TaskFragmentBinding viewBinding = TaskFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(TaskViewModel.class);

        TaskAdapter adapter = new TaskAdapter(taskId -> viewModel.onDeleteTaskButtonClicked(taskId));

        viewBinding.taskFragmentRecyclerView.setAdapter(adapter);
        viewBinding.taskFragmentFabAddTask.setOnClickListener(view -> navigationListener.displayAddTaskDialog());
        viewBinding.taskFragmentFabAddTask.setOnLongClickListener(view -> {
            viewModel.onAddButtonLongClicked();
            return true;
        });

        viewModel.getViewStateLiveData().observe(getViewLifecycleOwner(), taskViewStates -> {
            Log.d("Nino", "observe() called with: taskViewStates = [" + taskViewStates + "]");
            adapter.submitList(taskViewStates);
        });

        return viewBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.task_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_task) {
            viewModel.onSortButtonClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
