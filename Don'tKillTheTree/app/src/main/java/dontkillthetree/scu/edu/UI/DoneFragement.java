package dontkillthetree.scu.edu.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dontkillthetree.scu.edu.model.Project;

public class DoneFragement extends ListFragment {
    Context context;
    List<Project> projects;
    private String TAG = "CHENG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        projects = ProjectListActivity.projectList;
        List<Project> doneProjects = listCheck();
        DoneProjectsArrayAdapter adapter = new DoneProjectsArrayAdapter(context, R.layout.list_item_current_project, doneProjects);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        context = getActivity();
        projects = ProjectListActivity.projectList;
        List<Project> doneProjects = listCheck();
        DoneProjectsArrayAdapter adapter = new DoneProjectsArrayAdapter(context, R.layout.list_item_current_project, doneProjects);
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    private List<Project> listCheck(){
        List<Project> result = new ArrayList<Project>();
        for(Project p:projects){
            if(p.getCurrentMilestone() == null){
                result.add(p);
            }
        }
        return result;
    }
}

