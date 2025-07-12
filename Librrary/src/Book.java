import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private boolean isBorrowed;
    private String borrowedBy;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
        this.borrowedBy = null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    public String getBorrowedBy() {
        return borrowedBy != null ? borrowedBy : "-";
    }

    public void setBorrowedBy(String user) {
        this.borrowedBy = user;
    }

    public String getStatus() {
        return isBorrowed ? "Dipinjam" : "Tersedia";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}