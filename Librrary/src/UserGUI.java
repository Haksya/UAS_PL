import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserGUI extends JFrame {
    private Library library;
    private String username;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JTextField searchField;
    private JTextField idField;

    public UserGUI(Library library, String username) {
        this.library = library;
        this.username = username;
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Member: " + username + " - Sistem Peminjaman Buku");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Tabbed pane untuk buku dan riwayat
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel untuk daftar buku
        JPanel bookPanel = new JPanel(new BorderLayout(10, 10));
        String[] bookColumns = {"ID", "Judul", "Penulis", "Status"};
        bookTableModel = new DefaultTableModel(bookColumns, 0);
        bookTable = new JTable(bookTableModel);
        updateBookTable("");
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);

        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchPanel.add(new JLabel("Cari Buku:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        bookPanel.add(searchPanel, BorderLayout.NORTH);

        // Panel untuk input dan tombol
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel idLabel = new JLabel("ID Buku:");
        idField = new JTextField(10);
        JButton borrowButton = new JButton("Pinjam");
        JButton returnButton = new JButton("Kembalikan");
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(borrowButton);
        inputPanel.add(returnButton);
        bookPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel untuk riwayat peminjaman
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        String[] historyColumns = {"ID Buku", "Username", "Tanggal Pinjam", "Jatuh Tempo", "Tanggal Kembali"};
        historyTableModel = new DefaultTableModel(historyColumns, 0);
        historyTable = new JTable(historyTableModel);
        updateHistoryTable(username);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Daftar Buku", bookPanel);
        tabbedPane.addTab("Riwayat Peminjaman", historyPanel);
        add(tabbedPane, BorderLayout.CENTER);

        // Tombol kembali
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Kembali ke Login");
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        borrowButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Masukkan ID buku!");
                return;
            }
            boolean success = library.borrowBook(id, username);
            if (success) {
                JOptionPane.showMessageDialog(null, "Buku berhasil dipinjam!");
                updateBookTable(searchField.getText().trim());
                updateHistoryTable(username);
            } else {
                JOptionPane.showMessageDialog(null, "Buku tidak ditemukan atau sedang dipinjam!");
            }
            idField.setText("");
        });

        returnButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Masukkan ID buku!");
                return;
            }
            Book book = library.findBookById(id);
            if (book != null && book.isBorrowed() && !book.getBorrowedBy().equals(username)) {
                JOptionPane.showMessageDialog(null, "Buku ini dipinjam oleh pengguna lain!");
                return;
            }
            boolean success = library.returnBook(id);
            if (success) {
                JOptionPane.showMessageDialog(null, "Buku berhasil dikembalikan!");
                updateBookTable(searchField.getText().trim());
                updateHistoryTable(username);
            } else {
                JOptionPane.showMessageDialog(null, "Buku tidak ditemukan atau tidak sedang dipinjam!");
            }
            idField.setText("");
        });

        searchButton.addActionListener(e -> updateBookTable(searchField.getText().trim()));

        backButton.addActionListener(e -> {
            dispose();
            new LoginGUI(library).setVisible(true);
        });
    }

    private void updateBookTable(String keyword) {
        bookTableModel.setRowCount(0);
        List<Book> books = keyword.isEmpty() ? library.getBooks() : library.searchBooks(keyword);
        for (Book book : books) {
            bookTableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getStatus()});
        }
    }

    private void updateHistoryTable(String username) {
        historyTableModel.setRowCount(0);
        for (Object[] row : library.getBorrowHistory(username)) {
            historyTableModel.addRow(row);
        }
    }
}