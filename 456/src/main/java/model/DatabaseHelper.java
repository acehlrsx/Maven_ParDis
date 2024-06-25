package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:usersDB.db"; 

    private static void executeStatement(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "email TEXT, " +
                        "name TEXT, " +
                        "password TEXT)";
        executeStatement(sql);
    }
    public static void createItineraryTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS itineraries (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "username TEXT, " +
                     "date TEXT, " +
                     "travel_name TEXT, " +
                     "starting_location TEXT, " +
                     "etd TEXT, " +
                     "destination TEXT, " +
                     "eta TEXT, " +
                     "FOREIGN KEY(username) REFERENCES users(username))";
        executeStatement(sql);
    }

    public static boolean insertUser(String username, String email, String name, String password) {
        String sql = "INSERT INTO users(username, email, name, password) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, name);
            pstmt.setString(4, password);
            pstmt.executeUpdate();
            return true; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    public static void createDaysTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS days (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "itinerary_id INTEGER, " +
                     "day_number INTEGER, " +
                     "FOREIGN KEY(itinerary_id) REFERENCES itineraries(id))";
        executeStatement(sql);
    }
    
    public static void createPlacesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS places (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "day_id INTEGER, " +
                     "date TEXT, " +
                     "destination TEXT, " +
                     "FOREIGN KEY(day_id) REFERENCES days(id))";
        executeStatement(sql);
    }
}

