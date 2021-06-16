package fr.delcey.todoc.ui.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import fr.delcey.todoc.R;
import fr.delcey.todoc.databinding.AddTaskProjectSpinnerItemBinding;

class AddTaskProjectSpinnerAdapter extends ArrayAdapter<AddTaskViewStateItem> {
    public AddTaskProjectSpinnerAdapter(@NonNull Context context) {
        super(context, R.layout.add_task_project_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    public View getCustomView(int position, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AddTaskProjectSpinnerItemBinding binding = AddTaskProjectSpinnerItemBinding.inflate(inflater, parent, false);

        AddTaskViewStateItem item = getItem(position);

        assert item != null;

        binding.addTaskProjectItemImageViewColor.setColorFilter(item.getProjectColor());
        binding.addTaskProjectItemTextViewName.setText(item.getProjectName());

        return binding.getRoot();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((AddTaskViewStateItem) resultValue).getProjectName();
            }
        };
    }
}
