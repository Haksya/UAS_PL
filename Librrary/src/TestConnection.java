import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/library_db?useSSL=false";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Koneksi berhasil ke MySQL!");
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver tidak ditemukan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Gagal koneksi ke database.");
            e.printStackTrace();
        }
    }
}