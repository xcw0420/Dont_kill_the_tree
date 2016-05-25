package dontkillthetree.scu.edu.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import dontkillthetree.scu.edu.model.Project;
import dontkillthetree.scu.edu.model.Projects;

public class ProjectListActivity extends ParentActivity implements AdapterView.OnItemClickListener{
    private Context context = this;
    private List<Project> projectList = new ArrayList<>();
    private ListView projectListView;
    private ArrayAdapter<Project> mAdapter;
    private String TAG = "SEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
//        context = ProjectListActivity.this;

        //Get the ListView
        projectListView = (ListView)findViewById(R.id.projectListView);

        //Populate the arrayList with Project object
        try {
            projectList = Projects.getAllProjects(context);
        } catch(ParseException ex) {
            Log.i(TAG, ex.toString());
        }

        //Set arrayAdapter
        mAdapter = new ProjectsArrayAdapter(this, R.layout.project_row, projectList);
        projectListView.setAdapter(mAdapter);
        projectListView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
        toastShow("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.notifyDataSetChanged();
        toastShow("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        toastShow("onResume");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Project mProject = projectList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectListActivity.this);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle("Choosing")
                .setMessage("What do you want to do next? ")
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // link to project detail activity
                        Intent intent = new Intent(ProjectListActivity.this, ProjectDetailActivity.class);
                        intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID_FROM_LIST, mProject.getId());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // update the status of milestone as COMPLETED
                        mProject.getCurrentMilestone().setCompleted(true);
                    }
                })
                .setCancelable(true);
        builder.create().show();
    }



    private void toastShow(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
