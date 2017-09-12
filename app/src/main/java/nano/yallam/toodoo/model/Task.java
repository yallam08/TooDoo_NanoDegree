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
}
