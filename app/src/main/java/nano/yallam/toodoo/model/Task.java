package nano.yallam.toodoo.model;


public class Task {
    private String title;
    private String note;
    private long due;

    public Task() {
    }

    public Task(String title, String note, long due) {
        this.title = title;
        this.note = note;
        this.due = due;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDue() {
        return due;
    }

    public void setDue(long due) {
        this.due = due;
    }


    /**
     * Override to easily compare Task objects
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Task.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Task other = (Task) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.note == null) ? (other.note != null) : !this.note.equals(other.note)) {
            return false;
        }
        if (this.due != other.due) {
            return false;
        }
        return true;
    }

    /**
     * Override to easily compare Task objects
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 53 * hash + (this.note != null ? this.note.hashCode() : 0);
        hash = 53 * hash + (int) this.due;
        return hash;
    }
}
