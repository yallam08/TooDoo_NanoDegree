package nano.yallam.toodoo.ui.widget;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nano.yallam.toodoo.R;
import nano.yallam.toodoo.model.Task;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TasksService extends IntentService {

    public static final String ACTION_UPDATE_TASKS = "nano.yallam.toodoo.action.update_tasks";

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mTasksDatabaseReference = mFirebaseDatabase.getReference().child("tasks");
    private ChildEventListener mChildEventListener;

    public TasksService() {
        super("TasksService");
    }

    public static void startActionUpdateTasks(Context context) {
        Intent intent = new Intent(context, TasksService.class);
        intent.setAction(ACTION_UPDATE_TASKS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_TASKS.equals(action)) {
                GridWidgetService.mTasks.clear();

                if (mChildEventListener == null) {
                    mChildEventListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Task task = dataSnapshot.getValue(Task.class);
                            GridWidgetService.mTasks.add(task);

                            handleActionUpdateWidgetGrid();
                        }

                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            Task task = dataSnapshot.getValue(Task.class);
                            GridWidgetService.mTasks.remove(task);

                            handleActionUpdateWidgetGrid();
                        }

                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    mTasksDatabaseReference.addChildEventListener(mChildEventListener);
                }
            }
        }
    }

    private void handleActionUpdateWidgetGrid() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TasksWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
        //Now update all widgets
        TasksWidgetProvider.updateTasksGrid(this, appWidgetManager, appWidgetIds);
    }
}
