package fr.delcey.todoc.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import fr.delcey.todoc.databinding.TaskEmptyStateItemBinding;
import fr.delcey.todoc.databinding.TaskHeaderItemBinding;
import fr.delcey.todoc.databinding.TaskItemBinding;

public class TaskAdapter extends ListAdapter<TaskViewState, RecyclerView.ViewHolder> {
    @NonNull
    private final TaskListener listener;

    public TaskAdapter(@NonNull TaskListener listener) {
        super(new TaskDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (TaskViewState.Type.values()[viewType]) {
            case HEADER:
                return new TaskHeaderViewHolder(
                    TaskHeaderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
                );
            case TASK:
                return new TaskViewHolder(
                    TaskItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
                );
            case EMPTY_STATE:
                return new RecyclerView.ViewHolder(
                    TaskEmptyStateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot()
                ) {
                };
            default:
                throw new IllegalStateException("Unknown viewType : " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskHeaderViewHolder) {
            ((TaskHeaderViewHolder) holder).bind((TaskViewState.Header) getItem(position));
        } else if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).bind((TaskViewState.Task) getItem(position), listener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().ordinal();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final TaskItemBinding binding;

        public TaskViewHolder(@NonNull TaskItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(@NonNull TaskViewState.Task item, @NonNull TaskListener listener) {
            binding.taskItemImageViewColor.setColorFilter(item.getProjectColor());
            binding.taskItemTextViewDescription.setText(item.getDescription());
            binding.taskItemImageViewDelete.setOnClickListener(view -> listener.onDeleteTaskButtonClicked(item.getTaskId()));
        }
    }

    public static class TaskHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TaskHeaderItemBinding binding;

        public TaskHeaderViewHolder(@NonNull TaskHeaderItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(@NonNull TaskViewState.Header item) {
            binding.taskHeaderItemTextViewTitle.setText(item.getTitle());
        }
    }

    public static class TaskDiffCallback extends DiffUtil.ItemCallback<TaskViewState> {
        @Override
        public boolean areItemsTheSame(@NonNull TaskViewState oldItem, @NonNull TaskViewState newItem) {
            return (
                oldItem instanceof TaskViewState.Header && newItem instanceof TaskViewState.Header
                    && ((TaskViewState.Header) oldItem).getTitle().equals(((TaskViewState.Header) newItem).getTitle())
            ) || (
                oldItem instanceof TaskViewState.Task && newItem instanceof TaskViewState.Task
                    && ((TaskViewState.Task) oldItem).getTaskId() == ((TaskViewState.Task) newItem).getTaskId()
            ) || (
                oldItem instanceof TaskViewState.EmptyState && newItem instanceof TaskViewState.EmptyState
            );
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskViewState oldItem, @NonNull TaskViewState newItem) {
            return oldItem.equals(newItem);
        }
    }
}
