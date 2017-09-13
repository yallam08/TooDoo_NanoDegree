package nano.yallam.toodoo.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import nano.yallam.toodoo.R;
import nano.yallam.toodoo.model.Task;

import static nano.yallam.toodoo.ui.widget.GridWidgetService.mTasks;


public class GridWidgetService extends RemoteViewsService {
    public static ArrayList<Task> mTasks = new ArrayList<>();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;


    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mTasks == null) return 0;
        return mTasks.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mTasks == null || mTasks.size() == 0) {
            return null;
        }

        Task task = mTasks.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        String taskTitle = task.getTitle();
        views.setTextViewText(R.id.tv_widget_tasks_title, taskTitle);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

