import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public Library() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO books (id, title, author, is_borrowed, borrowed_by) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, book.getId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setBoolean(4, book.isBorrowed());
            stmt.setString(5, book.getBorrowedBy());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteBook(String id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Periksa apakah buku sedang dipinjam
            Book book = findBookById(id);
            if (book != null && book.isBorrowed()) {
                return false; // Buku sedang dipinjam, tidak bisa dihapus
            }

            // Hapus entri dari borrow_history terlebih dahulu
            String deleteHistorySql = "DELETE FROM borrow_history WHERE book_id = ?";
            PreparedStatement deleteHistoryStmt = conn.prepareStatement(deleteHistorySql);
            deleteHistoryStmt.setString(1, id);
            deleteHistoryStmt.executeUpdate();

            // Hapus buku dari tabel books
            String deleteBookSql = "DELETE FROM books WHERE id = ?";
            PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookSql);
            deleteBookStmt.setString(1, id);
            int rowsAffected = deleteBookStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(String id, String title, String author) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE books SET title = ?, author = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Book book = new Book(rs.getString("id"), rs.getString("title"), rs.getString("author"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                book.setBorrowedBy(rs.getString("borrowed_by"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM books WHERE id = ? OR title LIKE ? OR author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(rs.getString("id"), rs.getString("title"), rs.getString("author"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                book.setBorrowedBy(rs.getString("borrowed_by"));
                results.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Book findBookById(String id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Book book = new Book(rs.getString("id"), rs.getString("title"), rs.getString("author"));
                book.setBorrowed(rs.getBoolean("is_borrowed"));
                book.setBorrowedBy(rs.getString("borrowed_by"));
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean borrowBook(String id, String username) {
        Book book = findBookById(id);
        if (book == null || book.isBorrowed()) {
            return false;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE books SET is_borrowed = ?, borrowed_by = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, true);
            stmt.setString(2, username);
            stmt.setString(3, id);
            stmt.executeUpdate();
            logBorrow(id, username);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(String id) {
        Book book = findBookById(id);
        if (book == null || !book.isBorrowed()) {
            return false;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE books SET is_borrowed = ?, borrowed_by = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, false);
            stmt.setNull(2, Types.VARCHAR);
            stmt.setString(3, id);
            stmt.executeUpdate();
            logReturn(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logBorrow(String bookId, String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO borrow_history (book_id, username, due_date) VALUES (?, ?, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 DAY))";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logReturn(String bookId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE borrow_history SET return_date = CURRENT_TIMESTAMP WHERE book_id = ? AND return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getBorrowHistory(String username) {
        List<Object[]> history = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = username == null ?
                    "SELECT book_id, username, borrow_date, due_date, return_date FROM borrow_history" :
                    "SELECT book_id, username, borrow_date, due_date, return_date FROM borrow_history WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (username != null) stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                history.add(new Object[]{
                        rs.getString("book_id"),
                        rs.getString("username"),
                        rs.getTimestamp("borrow_date"),
                        rs.getTimestamp("due_date"),
                        rs.getTimestamp("return_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public boolean registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT password FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}