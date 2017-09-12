package nano.yallam.toodoo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nano.yallam.toodoo.R;
import nano.yallam.toodoo.model.Task;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListAdapterViewHolder> {

    private Context mContext;
    private final TaskListAdapterOnClickHandler mClickHandler;

    private List<Task> tasks;


    public interface TaskListAdapterOnClickHandler {
        void onClick(Task task);
    }


    public TaskListAdapter(Context context, List<Task> tasks, TaskListAdapterOnClickHandler clickHandler) {
        mContext = context;
        this.tasks = tasks;
        mClickHandler = clickHandler;
    }

    class TaskListAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        final TextView mTaskTitle;
        final TextView mTaskNote;

        TaskListAdapterViewHolder(View view) {
            super(view);
            mTaskTitle = (TextView) view.findViewById(R.id.task_title);
            mTaskNote = (TextView) view.findViewById(R.id.task_note);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(tasks.get(adapterPosition));
        }
    }

    @Override
    public TaskListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_task_list, viewGroup, false);
        return new TaskListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskListAdapterViewHolder viewHolder, int position) {
        viewHolder.mTaskTitle.setText(tasks.get(position).getTitle());
        viewHolder.mTaskNote.setText(tasks.get(position).getNote());
    }

    @Override
    public int getItemCount() {
        if (null == tasks) return 0;
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        notifyDataSetChanged();
    }
}