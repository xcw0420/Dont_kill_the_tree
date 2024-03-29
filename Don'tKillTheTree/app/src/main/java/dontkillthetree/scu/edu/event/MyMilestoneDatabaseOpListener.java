package dontkillthetree.scu.edu.event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

import dontkillthetree.scu.edu.Util.Util;
import dontkillthetree.scu.edu.database.DatabaseContract;
import dontkillthetree.scu.edu.database.DatabaseHelper;

public class MyMilestoneDatabaseOpListener implements MilestoneDatabaseOpListener {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Context context;

    public MyMilestoneDatabaseOpListener(Context context) {
        this.context = context;
    }

    public String[] onSelect(long id) {
        String[] result = new String[3];
        databaseHelper = new DatabaseHelper(this.context);
        db = databaseHelper.getWritableDatabase();

        String[] projection = {
                DatabaseContract.MilestoneEntry.COLUMN_NAME_NAME,
                DatabaseContract.MilestoneEntry.COLUMN_NAME_DUE_DATE,
                DatabaseContract.MilestoneEntry.COLUMN_NAME_COMPLETED
        };
        String selection = DatabaseContract.MilestoneEntry._ID + " = " + id;
        Cursor cursor = db.query(DatabaseContract.MilestoneEntry.TABLE_NAME, projection, selection, null, null, null, null);
        cursor.moveToFirst();
        result[0] = cursor.getString(cursor.getColumnIndex(DatabaseContract.MilestoneEntry.COLUMN_NAME_NAME));
        result[1] = cursor.getString(cursor.getColumnIndex(DatabaseContract.MilestoneEntry.COLUMN_NAME_DUE_DATE));
        result[2] = String.valueOf(cursor.getInt(cursor.getColumnIndex(DatabaseContract.MilestoneEntry.COLUMN_NAME_COMPLETED)));

        cursor.close();
        db.close();
        databaseHelper.close();

        return result;
    }

    public long onInsert(String name, Calendar dueDate) {
        long id;
        databaseHelper = new DatabaseHelper(this.context);
        db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MilestoneEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.MilestoneEntry.COLUMN_NAME_DUE_DATE, Util.calendarToString(dueDate));
        values.put(DatabaseContract.MilestoneEntry.COLUMN_NAME_IS_ON_TIME, true);
        values.put(DatabaseContract.MilestoneEntry.COLUMN_NAME_COMPLETED, false);
        id = db.insert(DatabaseContract.MilestoneEntry.TABLE_NAME, "null", values);

        db.close();
        databaseHelper.close();

        return id;
    }

    public void onUpdate(PropertyChangeEvent event) {
        databaseHelper = new DatabaseHelper(this.context);
        db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (event.getPropertyName().equals(DatabaseContract.MilestoneEntry.COLUMN_NAME_COMPLETED)) {
            values.put(event.getPropertyName(), event.getValue().equals("true") ? 1 : 0);
        }
        else if (event.getPropertyName().equals(DatabaseContract.MilestoneEntry.COLUMN_NAME_IS_ON_TIME)) {
            values.put(event.getPropertyName(), event.getValue().equals("true") ? 1 : 0);
        }
        else {
            values.put(event.getPropertyName(), event.getValue());
        }

        String selection = DatabaseContract.MilestoneEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(event.getId())};
        db.update(DatabaseContract.MilestoneEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
        databaseHelper.close();
    }

    public void onDelete(DisposeEvent event) {
        databaseHelper = new DatabaseHelper(this.context);
        db = databaseHelper.getWritableDatabase();

        String milestoneToDelete = DatabaseContract.MilestoneEntry._ID + " = " + event.getId();
        String projectMilestoneToDelete = DatabaseContract.ProjectMilestoneEntry.COLUMN_NAME_MILESTONE_ID + " = " + event.getId();
        db.delete(DatabaseContract.ProjectMilestoneEntry.TABLE_NAME, projectMilestoneToDelete, null);
        db.delete(DatabaseContract.MilestoneEntry.TABLE_NAME, milestoneToDelete, null);

        db.close();
        databaseHelper.close();
    }
}
