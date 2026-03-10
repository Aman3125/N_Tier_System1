package daoexample.domain;

/// <summary>
/// Represents one task row from the database.
/// </summary>
public class Task {

    private int _id;
    private String _title;
    private String _status;

    public Task(int id, String title, String status) {
        if (id < 0)
            throw new IllegalArgumentException("id must be >= 0");

        if (title == null || title.isBlank())
            throw new IllegalArgumentException("title is required");

        if (status == null || status.isBlank())
            throw new IllegalArgumentException("status is required");

        _id = id;
        _title = title.trim();
        _status = status.trim().toUpperCase();
    }

    public int getId() {
        return _id;
    }

    public String getTitle() {
        return _title;
    }

    public String getStatus() {
        return _status;
    }

    @Override
    public String toString() {
        return "Task{id=" + _id + ", title='" + _title + "', status='" + _status + "'}";
    }
}