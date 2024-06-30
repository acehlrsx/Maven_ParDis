package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:Maven_ParDis/456/db/usersDB.db"; 

    // Helper method to execute statements without results
    private static void executeStatement(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    // Create the users table
    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "email TEXT, " +
                        "name TEXT, " +
                        "password TEXT)";
        executeStatement(sql);
    }

    // Insert a new user into the users table
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

    // Authenticate a user
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

    // Create the itineraries table
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

    // Create the days table
    public static void createDaysTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS days (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "itinerary_id INTEGER, " +
                     "day_number INTEGER, " +
                     "FOREIGN KEY(itinerary_id) REFERENCES itineraries(id))";
        executeStatement(sql);
    }

    // Create the places table
    public static void createPlacesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS places (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "day_id INTEGER, " +
                     "time TEXT, " +
                     "destination TEXT, " +
                     "photo TEXT, " +  // Added photo column
                     "FOREIGN KEY(day_id) REFERENCES days(id))";
        executeStatement(sql);
    }

    // Insert a new itinerary and return its ID
    public static int insertItinerary(String username, String travelName, String date, String startingLocation, String etd, String destination, String eta) throws SQLException {
        String sql = "INSERT INTO itineraries(username, travel_name, date, starting_location, etd, destination, eta) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, travelName);
            pstmt.setString(3, date);
            pstmt.setString(4, startingLocation);
            pstmt.setString(5, etd);
            pstmt.setString(6, destination);
            pstmt.setString(7, eta);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    // Insert a new day into the days table
    public static void insertDay(int itineraryId, int dayNumber) throws SQLException {
        String sql = "INSERT INTO days(itinerary_id, day_number) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itineraryId);
            pstmt.setInt(2, dayNumber);
            pstmt.executeUpdate();
        }
    }

    // Insert a new place into the places table
    public static void insertPlace(int dayId, String time, String destination, String photo) throws SQLException {
        String sql = "INSERT INTO places(day_id, time, destination, photo) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dayId);
            pstmt.setString(2, time);
            pstmt.setString(3, destination);
            pstmt.setString(4, photo);
            pstmt.executeUpdate();
        }
    }

    // Retrieve the last inserted ID
    public static int getLastInsertId() throws SQLException {
        String sql = "SELECT last_insert_rowid()";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to get the inserted ID.");
            }
        }
    }

    //Retrieve all itineraries for a specific user where done = 0 (not done)
    public static List<String> getNotDoneTravelNames(String username) {
        List<String> travelNames = new ArrayList<>();
        String sql = "SELECT travel_name FROM itineraries WHERE username = ? AND done = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, 0); // 0 means not done

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    travelNames.add(rs.getString("travel_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
        }
        return travelNames;
    }

    // Retrieve details of a specific itinerary by its ID
    public static List<String> getItineraryById(int itineraryId) throws SQLException {
        List<String> itineraryDetails = new ArrayList<>();
        String sql = "SELECT * FROM itineraries WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itineraryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    itineraryDetails.add(rs.getString("travel_name"));
                    itineraryDetails.add(rs.getString("date"));
                    itineraryDetails.add(rs.getString("starting_location"));
                    itineraryDetails.add(rs.getString("etd"));
                    itineraryDetails.add(rs.getString("destination"));
                    itineraryDetails.add(rs.getString("eta"));
                }
            }
        }
        return itineraryDetails;
    }

    // Retrieve travel names for a specific user
    public static List<String> getTravelNames(String username) {
        List<String> travelNames = new ArrayList<>();
        String sql = "SELECT travel_name FROM itineraries WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    travelNames.add(rs.getString("travel_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
        }
        return travelNames;
    }
    public static boolean deleteItinerary(String travelName) throws SQLException {
        String sqlDeleteItinerary = "DELETE FROM itineraries WHERE travel_name = ?";
        String sqlDeleteDays = "DELETE FROM days WHERE itinerary_id = (SELECT id FROM itineraries WHERE travel_name = ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmtDeleteDays = conn.prepareStatement(sqlDeleteDays);
             PreparedStatement pstmtDeleteItinerary = conn.prepareStatement(sqlDeleteItinerary)) {
            pstmtDeleteDays.setString(1, travelName);
            pstmtDeleteItinerary.setString(1, travelName);
            
            int rowsDeleted = pstmtDeleteItinerary.executeUpdate();
            if (rowsDeleted > 0) {
                pstmtDeleteDays.executeUpdate();
                return true;
            } else {
                return false;
            }
        }
    }
    public static boolean markItineraryAsDone(String travelName) {
        String sql = "UPDATE itineraries SET done = ? WHERE travel_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setBoolean(1, true);
            pstmt.setString(2, travelName);
    
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<String> getCompletedItineraries(String username) throws SQLException {
        List<String> completedItineraries = new ArrayList<>();
        String sql = "SELECT travel_name FROM itineraries WHERE username = ? AND done = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, username);
            pstmt.setBoolean(2, true);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    completedItineraries.add(rs.getString("travel_name"));
                }
            }
        }
        return completedItineraries;
    }
    public static List<String> getItineraryByTravelName(String travelName) throws SQLException {
        List<String> itineraryDetails = new ArrayList<>();
        String sql = "SELECT * FROM itineraries WHERE travel_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, travelName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    itineraryDetails.add(rs.getString("username"));
                    itineraryDetails.add(rs.getString("date"));
                    itineraryDetails.add(rs.getString("starting_location"));
                    itineraryDetails.add(rs.getString("etd"));
                    itineraryDetails.add(rs.getString("destination"));
                    itineraryDetails.add(rs.getString("eta"));
                    return itineraryDetails;
                }
            }
        }
        return null; // Return null if no itinerary found with the given travel name
    }
}
