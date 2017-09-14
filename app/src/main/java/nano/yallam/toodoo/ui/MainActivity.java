package nano.yallam.toodoo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nano.yallam.toodoo.BuildConfig;
import nano.yallam.toodoo.R;
import nano.yallam.toodoo.model.Task;
import nano.yallam.toodoo.ui.adapter.TaskListAdapter;
import nano.yallam.toodoo.util.Utils;

public class MainActivity extends AppCompatActivity implements TaskListAdapter.TaskListAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String ANONYMOUS = "anonymous";

    public static final int RC_SIGN_IN = 1;

    public static final String EXTRA_TASK_TITLE = "extra_task_title";

    private ProgressDialog mProgressDialog;

    @BindView(R.id.rv_tasks)
    RecyclerView mTasksRecyclerView;
    private TaskListAdapter mTaskListAdapter;

    @BindView(R.id.input_create_task)
    EditText mInputCreateTask;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTasksDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("");

        if (mProgressDialog == null) {
            mProgressDialog = Utils.generateProgressDialog(this, null, false);
        }

        LinearLayoutManager tasksLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTasksRecyclerView.setLayoutManager(tasksLayoutManager);
        mTaskListAdapter = new TaskListAdapter(this, new ArrayList<Task>(), this);
        mTasksRecyclerView.setAdapter(mTaskListAdapter);


        mUsername = ANONYMOUS;

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mTasksDatabaseReference = mFirebaseDatabase.getReference().child("tasks");


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        showBannerAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mTaskListAdapter.setTasks(new ArrayList<Task>());
        detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mTaskListAdapter.setTasks(new ArrayList<Task>());
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Task task = dataSnapshot.getValue(Task.class);
                    mTaskListAdapter.addTask(task);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Task task = dataSnapshot.getValue(Task.class);
                    mTaskListAdapter.removeTask(task);
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mTasksDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mTasksDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    /**
     * Handle click on a task item
     *
     * @param task the clicked task object
     */
    @Override
    public void onClick(Task task) {
        //TODO
    }


    @OnClick(R.id.btn_create_task)
    public void openCreateNewTaskActivity() {
        Intent createTaskIntent = new Intent(MainActivity.this, CreateTaskActivity.class);
        String taskTitle = mInputCreateTask.getText().toString();
        if (!TextUtils.isEmpty(taskTitle)) {
            createTaskIntent.putExtra(EXTRA_TASK_TITLE, taskTitle);
        }
        startActivity(createTaskIntent);
    }


    private void showBannerAd() {
        final AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.setVisibility(View.GONE);
            }
        });
    }
}
