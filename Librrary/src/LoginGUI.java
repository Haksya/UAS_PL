import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    private Library library;

    public LoginGUI(Library library) {
        this.library = library;
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Login - Sistem Perpustakaan");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel label = new JLabel("Selamat Datang Di Aplikasi Perpustakaan", SwingConstants.CENTER);
        JButton adminButton = new JButton("Masuk sebagai Admin");
        JButton userButton = new JButton("Masuk sebagai Member");
        JButton registerButton = new JButton("Daftar sebagai Member");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(adminButton);
        buttonPanel.add(userButton);
        buttonPanel.add(registerButton);

        add(new JPanel());
        add(label);
        add(buttonPanel);
        add(new JPanel());

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Buat dialog kustom untuk input kata sandi
                JPasswordField passwordField = new JPasswordField(20);
                JPanel panel = new JPanel();
                panel.add(new JLabel("Masukkan kata sandi Admin:"));
                panel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Login Admin",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String password = new String(passwordField.getPassword());
                    if ("admin123".equals(password)) {
                        dispose();
                        new AdminGUI(library).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Kata sandi salah!");
                    }
                }
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField usernameField = new JTextField(10);
                JPasswordField passwordField = new JPasswordField(10);
                JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("Username:"));
                panel.add(usernameField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Masuk sebagai Member", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String username = usernameField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();
                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Username dan password tidak boleh kosong!");
                        return;
                    }
                    if (library.validateUser(username, password)) {
                        dispose();
                        new UserGUI(library, username).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Username atau password salah!");
                    }
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField usernameField = new JTextField(10);
                JPasswordField passwordField = new JPasswordField(10);
                JPasswordField confirmPasswordField = new JPasswordField(10);
                JPanel panel = new JPanel(new GridLayout(3, 2));
                panel.add(new JLabel("Username:"));
                panel.add(usernameField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);
                panel.add(new JLabel("Konfirmasi Password:"));
                panel.add(confirmPasswordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Daftar Member Baru", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String username = usernameField.getText().trim();
                    String password = new String(passwordField.getPassword()).trim();
                    String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
                    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Semua kolom harus diisi!");
                        return;
                    }
                    if (!password.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(null, "Password dan konfirmasi password tidak cocok!");
                        return;
                    }
                    if (library.registerUser(username, password)) {
                        JOptionPane.showMessageDialog(null, "Registrasi berhasil! Silakan login.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Username sudah digunakan!");
                    }
                }
            }
        });
    }
}