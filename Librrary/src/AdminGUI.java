import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminGUI extends JFrame {
    private Library library;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JTextField searchField;

    public AdminGUI(Library library) {
        this.library = library;
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Admin - Sistem Peminjaman Buku");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Tabbed pane untuk buku dan riwayat
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel untuk daftar buku
        JPanel bookPanel = new JPanel(new BorderLayout(10, 10));
        String[] bookColumns = {"ID", "Judul", "Penulis", "Status", "Dipinjam Oleh"};
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

        // Panel untuk input buku
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel idLabel = new JLabel("ID Buku:");
        JTextField idField = new JTextField(10);
        JLabel titleLabel = new JLabel("Judul Buku:");
        JTextField titleField = new JTextField(10);
        JLabel authorLabel = new JLabel("Penulis:");
        JTextField authorField = new JTextField(10);
        JButton addBookButton = new JButton("Tambah Buku");
        JButton deleteBookButton = new JButton("Hapus Buku");
        JButton editBookButton = new JButton("Edit Buku");

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(authorLabel);
        inputPanel.add(authorField);
        inputPanel.add(addBookButton);
        inputPanel.add(deleteBookButton);
        inputPanel.add(editBookButton);
        bookPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel untuk riwayat peminjaman
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        String[] historyColumns = {"ID Buku", "Username", "Tanggal Pinjam", "Jatuh Tempo", "Tanggal Kembali"};
        historyTableModel = new DefaultTableModel(historyColumns, 0);
        historyTable = new JTable(historyTableModel);
        updateHistoryTable(null);
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
        addBookButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua kolom harus diisi!");
                return;
            }

            if (library.findBookById(id) != null) {
                JOptionPane.showMessageDialog(null, "ID Buku sudah ada!");
                return;
            }

            library.addBook(new Book(id, title, author));
            JOptionPane.showMessageDialog(null, "Buku berhasil ditambahkan!");
            updateBookTable(searchField.getText().trim());
            idField.setText("");
            titleField.setText("");
            authorField.setText("");
        });

        deleteBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) bookTableModel.getValueAt(selectedRow, 0);
                Book book = library.findBookById(id);
                if (book != null && book.isBorrowed()) {
                    JOptionPane.showMessageDialog(null, "Buku sedang dipinjam dan tidak dapat dihapus!");
                    return;
                }
                if (library.deleteBook(id)) {
                    JOptionPane.showMessageDialog(null, "Buku berhasil dihapus!");
                    updateBookTable(searchField.getText().trim());
                    updateHistoryTable(null);
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menghapus buku!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Pilih buku yang akan dihapus!");
            }
        });

        editBookButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) bookTableModel.getValueAt(selectedRow, 0);
                String title = JOptionPane.showInputDialog("Masukkan judul baru:", bookTableModel.getValueAt(selectedRow, 1));
                String author = JOptionPane.showInputDialog("Masukkan penulis baru:", bookTableModel.getValueAt(selectedRow, 2));
                if (title != null && author != null && !title.trim().isEmpty() && !author.trim().isEmpty()) {
                    if (library.updateBook(id, title.trim(), author.trim())) {
                        JOptionPane.showMessageDialog(null, "Buku berhasil diperbarui!");
                        updateBookTable(searchField.getText().trim());
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal memperbarui buku!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Pilih buku yang akan diedit!");
            }
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
            bookTableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getStatus(), book.getBorrowedBy()});
        }
    }

    private void updateHistoryTable(String username) {
        historyTableModel.setRowCount(0);
        for (Object[] row : library.getBorrowHistory(null)) {
            historyTableModel.addRow(row);
        }
    }
}