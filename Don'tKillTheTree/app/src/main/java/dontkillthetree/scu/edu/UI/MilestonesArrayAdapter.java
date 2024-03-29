package dontkillthetree.scu.edu.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import dontkillthetree.scu.edu.Notification.CreateNotifyIntent;
import dontkillthetree.scu.edu.Util.Util;
import dontkillthetree.scu.edu.model.Milestone;


public class MilestonesArrayAdapter extends ArrayAdapter<Milestone> {
    private final List<Milestone> mMilestones;
    private Context context;
    private Calendar mProjectDueDate;

    public MilestonesArrayAdapter(Context context, int resource, List<Milestone> milestones) {
        super(context, resource, milestones);
        this.mMilestones = milestones;
        this.context = context;

        Milestone mLastMilestone = this.mMilestones.get(milestones.size() - 1);
        mProjectDueDate = mLastMilestone.getDueDate();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ScrapViewHolder holder;

        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.milestone_row, parent, false);

            holder = new ScrapViewHolder();
            holder.milestoneName = (TextView) row.findViewById(R.id.milestoneName);
            holder.milestoneDueDate = (TextView) row.findViewById(R.id.milestoneDueDate);
            row.setTag(holder);

        } else {
            holder = (ScrapViewHolder) row.getTag();
        }

        holder.milestoneName.setText(mMilestones.get(position).getName());
        if (mMilestones.get(position).isCompleted()){
            holder.milestoneName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
        else if (!mMilestones.get(position).isOnTime()){
            holder.milestoneName.setTextColor(ContextCompat.getColor(context, R.color.lightOrange));
        }
        holder.milestoneName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Milestone mMilestone = mMilestones.get(position);
                if (!mMilestone.isCompleted() && mMilestone.isOnTime()) {
                    // build a alertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // get the layout inflater
//                LayoutInflater inflater = context.getLayoutInflater(); // doesn't work
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    // inflate and set the layout for the dialog
                    final View alertDialogView = inflater.inflate(R.layout.milestone_name_alertdialog, null);
                    builder.setView(alertDialogView)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // get the new milestone name
                                    EditText ET_milestoneName = (EditText) alertDialogView.findViewById(R.id.editMilestoneName);
                                    String newMilestoneName = ET_milestoneName.getText().toString();
                                    if(!newMilestoneName.equals("")){
                                        // update the new milestone name into db
                                        mMilestone.setName(newMilestoneName);
                                        notifyDataSetChanged();
                                        //showToast("New milestone name set!");
                                    }else {
                                        showToast("Please input a valid name.");
                                    }

                                    // create/update notification
                                    // CreateNotifyIntent.makeIntent(context);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public  void onClick(DialogInterface dialog, int d) {
                                    //showToast("Cancel button clicked!");
//                                context.getDialog().cancel();
                                }
                            });
                    builder.create().show();
                }
            }
        });

        holder.milestoneDueDate.setText(Util.calendarToString(mMilestones.get(position).getDueDate()));
        holder.milestoneDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Milestone mMilestone = mMilestones.get(position);
                if (!mMilestone.isCompleted() && mMilestone.isOnTime()) {
                    // build a datePickerDialog
                    Calendar mCalendar = Calendar.getInstance();
                    // Use the current data as the default date in the picker
                    int mYear = mCalendar.get(Calendar.YEAR);
                    int mMonth = mCalendar.get(Calendar.MONTH);
                    int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            Calendar mCalendar = Calendar.getInstance();
                            mCalendar.set(year, month, day);
                            mMilestone.setDueDate(mCalendar);
                            notifyDataSetChanged();
                            showToast("New milestone due date set!");
                            // create/update notification
                            CreateNotifyIntent.makeIntent(context);
                        }
                    };
                    // Create a new instance of DatePickerDialog
                    DatePickerDialog mDatePickerDialog = new DatePickerDialog(context, mDateSetListener, mYear, mMonth, mDay);
                    mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    mDatePickerDialog.getDatePicker().setMaxDate(mProjectDueDate.getTimeInMillis());
                    mDatePickerDialog.show();
                }
            }
        });

        return row;
    }

    public class ScrapViewHolder {
        TextView milestoneName;
        TextView milestoneDueDate;
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
