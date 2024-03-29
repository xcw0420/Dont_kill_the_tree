package dontkillthetree.scu.edu.model;

import java.text.ParseException;
import java.util.Calendar;

import dontkillthetree.scu.edu.Util.Util;
import dontkillthetree.scu.edu.database.DatabaseContract;
import dontkillthetree.scu.edu.event.DisposeEvent;
import dontkillthetree.scu.edu.event.MilestoneDatabaseOpListener;
import dontkillthetree.scu.edu.event.PropertyChangeEvent;

public class Milestone implements Comparable{
    private final long id;
    private String name;
    private Calendar dueDate;
    private boolean onTime;
    private boolean completed;
    private boolean shown;
    private MilestoneDatabaseOpListener milestoneDatabaseOpListener;

    /**
     * Use this constructor when user creates a new milestone, i.e. no record in the database
     * @param name
     * @param dueDate
     */
    public Milestone(String name, Calendar dueDate, MilestoneDatabaseOpListener milestoneDatabaseOpListener) {
        Calendar currentDate = Calendar.getInstance();
        this.dueDate = (Calendar) dueDate.clone();
        Util.toNearestDueDate(this.dueDate);

        if (name == null || this.dueDate.before(currentDate)) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.onTime = true;
        this.completed = false;
        this.shown = false;
        this.milestoneDatabaseOpListener = milestoneDatabaseOpListener;
        this.id = this.milestoneDatabaseOpListener.onInsert(this.name, this.dueDate);
    }

    /**
     * Use this constructor when it is recovered from the database
     * @param id
     */
    public Milestone(long id, MilestoneDatabaseOpListener milestoneDatabaseOpListener) throws ParseException{
        this.milestoneDatabaseOpListener = milestoneDatabaseOpListener;
        String[] dbMilestone = this.milestoneDatabaseOpListener.onSelect(id);

        this.id = id;
        this.name = dbMilestone[0];
        Calendar dueDateCalendar = Util.stringToCalendar(dbMilestone[1]);
        this.dueDate = dueDateCalendar;
        this.completed = dbMilestone[2].equals("1") ? true : false;
        this.onTime = Util.isOnTime(dueDateCalendar, null) || this.completed;
        this.shown = false;
    }

    /**
     * Call this method to delete a milestone from the database
     */
    public void dispose() {
        milestoneDatabaseOpListener.onDelete(new DisposeEvent(id));
    }

    public int compareTo(Object milestone) {
        if (! (milestone instanceof Milestone)) {
            throw new IllegalArgumentException();
        }

        return dueDate.compareTo(((Milestone) milestone).getDueDate());
    }

    // getters and setters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // update database
        if (milestoneDatabaseOpListener != null) {
            milestoneDatabaseOpListener.onUpdate(new PropertyChangeEvent(
                    id,
                    DatabaseContract.MilestoneEntry.COLUMN_NAME_NAME,
                    name));
        }

        // update instance
        this.name = name;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        // update instance
        this.dueDate = (Calendar) dueDate.clone();
        Util.toNearestDueDate(this.dueDate);
        this.onTime = Util.isOnTime(this.dueDate, null) || this.completed;

        // update database
        if (milestoneDatabaseOpListener != null) {
            milestoneDatabaseOpListener.onUpdate(new PropertyChangeEvent(
                    id,
                    DatabaseContract.MilestoneEntry.COLUMN_NAME_DUE_DATE,
                    Util.calendarToString(this.dueDate)
            ));

            milestoneDatabaseOpListener.onUpdate(new PropertyChangeEvent(
                    id,
                    DatabaseContract.MilestoneEntry.COLUMN_NAME_IS_ON_TIME,
                    String.valueOf(onTime)
            ));
        }
    }

    public boolean isOnTime() {
        onTime = Util.isOnTime(dueDate, null) || completed;

        if (milestoneDatabaseOpListener != null) {
            milestoneDatabaseOpListener.onUpdate(new PropertyChangeEvent(
                    id,
                    DatabaseContract.MilestoneEntry.COLUMN_NAME_IS_ON_TIME,
                    String.valueOf(onTime)
            ));
        }

        return onTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        // update instance
        this.completed = completed;
        this.onTime = Util.isOnTime(this.dueDate, null) || this.completed;

        // update database
        if (milestoneDatabaseOpListener != null) {
            milestoneDatabaseOpListener.onUpdate(new PropertyChangeEvent(
                    id,
                    DatabaseContract.MilestoneEntry.COLUMN_NAME_COMPLETED,
                    String.valueOf(completed)
            ));

            milestoneDatabaseOpListener.onUpdate(new PropertyChangeEvent(
                    id,
                    DatabaseContract.MilestoneEntry.COLUMN_NAME_IS_ON_TIME,
                    String.valueOf(onTime)
            ));
        }
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
