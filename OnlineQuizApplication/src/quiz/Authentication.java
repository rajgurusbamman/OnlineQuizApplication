package quiz;

import java.sql.*;

public class Authentication {
    private static final String DB_URL = "jdbc:sqlite:quiz.db";

    public static boolean login(String username, String password) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return HashUtil.verifyPassword(password, storedHash);
                }
            }
        }
        return false;
    }

    public static void register(String username, String password) throws SQLException {
        String hashedPassword = HashUtil.hashPassword(password);
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        }
    }
}
