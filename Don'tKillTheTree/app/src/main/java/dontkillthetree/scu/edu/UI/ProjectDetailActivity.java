package dontkillthetree.scu.edu.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.util.List;

import dontkillthetree.scu.edu.Notification.CreateNotifyIntent;
import dontkillthetree.scu.edu.Util.Util;
import dontkillthetree.scu.edu.event.MyMilestoneDatabaseOpListener;
import dontkillthetree.scu.edu.event.MyProjectDatabaseOpListener;
import dontkillthetree.scu.edu.model.Milestone;
import dontkillthetree.scu.edu.model.Project;

public class ProjectDetailActivity extends ParentActivity implements AdapterView.OnItemClickListener {
    private Context context;
    private final String TAG = "Sen";
    private List<Milestone> mMilestones;
    private Project mProject;
    private long mProjectId;
    private android.app.AlertDialog.Builder builder;

    // views
    private TextView ET_projectName;
    private TextView ET_dueDate;
    private TextView ET_numberOfMilestone;
    private TextView ET_projectPartner;
    private ListView listView;


    // don't delete these
    public static final String EXTRA_PROJECT_NAME = "project_name";
    public static final String EXTRA_PROJECT_ID_FROM_CREATE = "project_id_from_create";
    public static final String EXTRA_PROJECT_ID_FROM_LIST = "project_id_from_list";
    public static final String EXTRA_ON_CREATE_PROCESS = "on_create_process";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        context = this;

        // Set background


        // get the widgets
        ET_projectName = (TextView) findViewById(R.id.projectName);
        ET_dueDate = (TextView) findViewById(R.id.dueDate);
        ET_numberOfMilestone = (TextView) findViewById(R.id.numberOfMilestone);
        ET_projectPartner = (TextView) findViewById(R.id.projectPartner);
        listView = (ListView) findViewById(R.id.listView);
        final TextView ET_projectPartner_Final = ET_projectPartner;

        // get data from db and create a list for listView

        if (getIntent().getExtras().get(EXTRA_PROJECT_ID_FROM_CREATE) != null) {
            mProjectId = (long) getIntent().getExtras().get(EXTRA_PROJECT_ID_FROM_CREATE);
        } else {
            mProjectId = (long) getIntent().getExtras().get(EXTRA_PROJECT_ID_FROM_LIST);
        }

        try {
            mProject = new Project(mProjectId, new MyProjectDatabaseOpListener(context), new MyMilestoneDatabaseOpListener(context));
            mMilestones = mProject.getMilestones();
        } catch (ParseException e) {
            Log.i(TAG, e.toString());
        }

        // set data to EditText
        ET_projectName.setText(mProject.getName());
        ET_dueDate.setText(Util.calendarToString(mProject.getDueDate()));
        ET_numberOfMilestone.setText(Integer.toString(mMilestones.size()));
        //ET_projectPartner.setText(mProject.getGuardianName());

        // Set up a dialog for project partner
        ET_projectPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // build a alertDialog
                builder = new android.app.AlertDialog.Builder(context);
                // get the layout inflater
//                LayoutInflater inflater = context.getLayoutInflater(); // doesn't work
                LayoutInflater inflater = (LayoutInflater) context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                // inflate and set the layout for the dialog
                final View alertDialogView = inflater.inflate(R.layout.project_guardian_alertdialog, null);
                builder.setView(alertDialogView)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // get the new guardian name
                                EditText ET_guardianName = (EditText) alertDialogView.findViewById(R.id.editGuardianName);
                                String newGuardianName = ET_guardianName.getText().toString();

                                EditText ET_gardianPhoneNumber = (EditText) alertDialogView.findViewById(R.id.editGuardianInfo);
                                String newGuardianPhoneNumber = ET_gardianPhoneNumber.getText().toString();


                                if(!(newGuardianName.equals("") || newGuardianPhoneNumber.equals(""))){
                                    // update the new guardian name into db
                                    mProject.setGuardianName(newGuardianName);
                                    mProject.setGuardianPhone(newGuardianPhoneNumber);
                                }else{
                                    Toast.makeText(ProjectDetailActivity.this, "Please input valid information.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public  void onClick(DialogInterface dialog, int d) {}
                        });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            mProject = new Project(mProjectId, new MyProjectDatabaseOpListener(context), new MyMilestoneDatabaseOpListener(context));
                        } catch (ParseException ex) {
                            Log.i(TAG, ex.toString());
                        }
                        // get the new guardian name
                        String newGuardianName = mProject.getGuardianName();
                        // TextView TV_guardianName = (TextView) findViewById(R.id.editGuardianName);
                        ET_projectPartner_Final.setText(newGuardianName);

                    }
                });

                builder.create().show();
            }
        });

        // create milestone listView in run-time
        listView.setAdapter(new MilestonesArrayAdapter(this, R.layout.milestone_row, mMilestones));
        listView.setOnItemClickListener(this);

        // get the save button
//        Button saveButton = (Button) findViewById(R.id.saveButton);
//        if (saveButton != null) {
//            saveButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // add data to db
//                }
//            });
//        } else {
//            toastShow("fail to get the save button!");
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SZhang", "onResume");
        try {
            mProject = new Project(mProjectId, new MyProjectDatabaseOpListener(context), new MyMilestoneDatabaseOpListener(context));
            mMilestones = mProject.getMilestones();
        } catch (ParseException e) {
            Log.i(TAG, e.toString());
        }
        listView.setAdapter(new MilestonesArrayAdapter(this, R.layout.milestone_row, mMilestones));
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // actually we don't need this one
    }

    private void toastShow(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            boolean onCreateProcess = (boolean) getIntent().getExtras().get(EXTRA_ON_CREATE_PROCESS);
            if (onCreateProcess){
                mProject.dispose();
                getIntent().putExtra(EXTRA_ON_CREATE_PROCESS, false);
                // create/update notification
                CreateNotifyIntent.makeIntent(context);
            }
        }catch (Exception e){
            Log.i("cxiong", "not on create process");
        }

    }

    // set menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save_project:
                // when click save project button in the action bar
                finish();
                // create/update notification
                CreateNotifyIntent.makeIntent(context);

                Intent intent = new Intent(this, ProjectListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
            default:
                return true;
        }
        return true;
    }

    public void showFullProjectName(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectDetailActivity.this);
        builder.setTitle("Project Name:")
                .setMessage(mProject.getName())
                .setCancelable(true);
        builder.create().show();
    }
}
