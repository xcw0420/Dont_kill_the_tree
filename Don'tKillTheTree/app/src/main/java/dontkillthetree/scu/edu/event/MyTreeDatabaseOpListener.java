package dontkillthetree.scu.edu.event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import dontkillthetree.scu.edu.database.DatabaseContract;
import dontkillthetree.scu.edu.database.DatabaseHelper;

/**
 * Created by Joey Zheng on 5/24/16.
 */
public class MyTreeDatabaseOpListener implements TreeDatabaseOpListener {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Context context;

    public MyTreeDatabaseOpListener(Context context) {
        this.context = context;
    }

    @Override
    public int[] onSelect() {
        int[] result = new int[2];
        databaseHelper = new DatabaseHelper(this.context);
        db = databaseHelper.getWritableDatabase();

        String[] projection = {DatabaseContract.TreeEntry.COLUMN_NAME_STAGE, DatabaseContract.TreeEntry.COLUMN_NAME_EXPERIENCE};
        Cursor cursor = db.query(DatabaseContract.TreeEntry.TABLE_NAME, projection, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            result[0] = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TreeEntry.COLUMN_NAME_STAGE));
            result[1] = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TreeEntry.COLUMN_NAME_EXPERIENCE));
        }
        else {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.TreeEntry.COLUMN_NAME_STAGE, 0);
            values.put(DatabaseContract.TreeEntry.COLUMN_NAME_EXPERIENCE, 0);
            db.insert(DatabaseContract.TreeEntry.TABLE_NAME, "null", values);
            result[0] = 0;
            result[1] = 1;
        }

        db.close();
        databaseHelper.close();

        return new int[0];
    }

    @Override
    public void onUpdate(PropertyChangeEvent event) {
        databaseHelper = new DatabaseHelper(this.context);
        db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(event.getPropertyName(), Integer.parseInt(event.getValue()));

        db.update(DatabaseContract.TreeEntry.TABLE_NAME, values, null, null);

        db.close();
        databaseHelper.close();
    }
}
