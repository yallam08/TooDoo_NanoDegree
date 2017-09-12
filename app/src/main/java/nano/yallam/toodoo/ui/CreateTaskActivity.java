package nano.yallam.toodoo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nano.yallam.toodoo.R;
import nano.yallam.toodoo.model.Task;

import static nano.yallam.toodoo.ui.MainActivity.EXTRA_TASK_TITLE;

public class CreateTaskActivity extends AppCompatActivity {

    @BindView(R.id.input_task_title)
    EditText mInputTaskTitle;
    @BindView(R.id.input_task_note)
    EditText mInputTaskNote;
    @BindView(R.id.input_task_due)
    EditText mInputTaskDue;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTasksDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(EXTRA_TASK_TITLE)) {
            mInputTaskTitle.setText(getIntent().getStringExtra(EXTRA_TASK_TITLE));
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTasksDatabaseReference = mFirebaseDatabase.getReference().child("tasks");
    }

    @OnClick(R.id.btn_create_task)
    public void createTask() {
        String taskTitle = mInputTaskTitle.getText().toString();
        if (TextUtils.isEmpty(taskTitle)) {
            mInputTaskTitle.setError(getString(R.string.error_invalid_task_title));
            return;
        }

        String taskNote = mInputTaskNote.getText().toString();

        long taskDue = new Date().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date parsedDate = dateFormat.parse(mInputTaskDue.getText().toString());
            taskDue = parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Task task = new Task(taskTitle, taskNote, taskDue);
        mTasksDatabaseReference.push().setValue(task);
        finish();
    }
}
