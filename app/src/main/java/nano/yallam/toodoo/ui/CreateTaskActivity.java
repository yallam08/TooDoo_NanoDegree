package nano.yallam.toodoo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nano.yallam.toodoo.R;
import nano.yallam.toodoo.model.Task;

import static nano.yallam.toodoo.ui.MainActivity.EXTRA_TASK_TITLE;

public class CreateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

        mInputTaskDue.setFocusable(false);
        mInputTaskDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        CreateTaskActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mInputTaskDue.setText(dayOfMonth + "-" + monthOfYear + "-" + year);

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                CreateTaskActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                true
        );
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mInputTaskDue.append(" " + hourOfDay + ":" + minute);
    }
}
