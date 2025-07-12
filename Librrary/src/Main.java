import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Library library = new Library();
            new LoginGUI(library).setVisible(true);
        });
    }
}