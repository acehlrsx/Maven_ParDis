package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class EmailData {
    String itineraryHtmlString;
    int itineraryId;
    String emailReceiveString;

    public EmailData(String itineraryHtmlString, String emailReceiveString, int itineraryId) {
        this.itineraryHtmlString = itineraryHtmlString;
        this.emailReceiveString = emailReceiveString;
        this.itineraryId = itineraryId;
    }
}
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:Maven_ParDis/456/db/usersDB.db"; 

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
                     "done BOOLEAN DEFAULT 0," +
                     "FOREIGN KEY(username) REFERENCES users(username))";
        executeStatement(sql);
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
                     "time TEXT, " +
                     "destination TEXT, " +
                     "photo TEXT, " +
                     "FOREIGN KEY(day_id) REFERENCES days(id))";
        executeStatement(sql);
    }

    public static int insertItinerary(Connection conn, String username, String travelName, String itineraryDate, String startingLocation, String etd, String destination, String eta) throws SQLException {
        String sql = "INSERT INTO itineraries (username, travel_name, date, starting_location, etd, destination, eta) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, travelName);
            pstmt.setString(3, itineraryDate);
            pstmt.setString(4, startingLocation);
            pstmt.setString(5, etd);
            pstmt.setString(6, destination);
            pstmt.setString(7, eta);
            pstmt.executeUpdate();
            return getLastInsertId(conn);
        }
    }
    
    public static void insertDay(Connection conn, int itineraryId, int dayNumber) throws SQLException {
        String sql = "INSERT INTO days (itinerary_id, day_number) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itineraryId);
            pstmt.setInt(2, dayNumber);
            pstmt.executeUpdate();
        }
    }    

    public static void insertPlace(Connection conn, int dayId, String time, String destination, String photo) throws SQLException {
        String sql = "INSERT INTO places(day_id, time, destination, photo) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dayId);
            pstmt.setString(2, time);
            pstmt.setString(3, destination);
            pstmt.setString(4, photo);
            pstmt.executeUpdate();
            System.out.println("Place inserted successfully for dayId: " + dayId);
        } catch (SQLException e) {
            System.err.println("Failed to insert place: " + e.getMessage());
            throw e;
        }
    }    

    public static int getLastInsertId(Connection conn) throws SQLException {
        String sql = "SELECT last_insert_rowid()";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to get the inserted ID.");
            }
        }
    }

    public static List<String> getNotDoneTravelNames(String username) {
        List<String> travelNames = new ArrayList<>();
        String sql = "SELECT travel_name FROM itineraries WHERE username = ? AND done = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, 0);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    travelNames.add(rs.getString("travel_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return travelNames;
    }

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
                    itineraryDetails.add(rs.getString("username"));
                }
            }
        }
        return itineraryDetails;
    }

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
            e.printStackTrace();
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
    public static boolean markItineraryAsDone(String username, String travelName) {
        String sql = "UPDATE itineraries SET done = ? WHERE travel_name = ? AND username = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            System.out.println("Marking itinerary as done for username: " + username + ", travelName: " + travelName); // Log input values

            pstmt.setBoolean(1, true);
            pstmt.setString(2, travelName);
            pstmt.setString(3, username);
    
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated); // Log result
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
    public static List<String> getItineraryByTravelName(String username, String travelName) throws SQLException {
        List<String> itineraryDetails = new ArrayList<>();
        String sql = "SELECT * FROM itineraries WHERE travel_name = ? AND username = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, travelName);
            pstmt.setString(2, username);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // Check if a row was found
                    itineraryDetails.add(rs.getString("username"));
                    itineraryDetails.add(rs.getString("date"));
                    itineraryDetails.add(rs.getString("travel_name"));
                    itineraryDetails.add(rs.getString("starting_location"));
                    itineraryDetails.add(rs.getString("etd"));
                    itineraryDetails.add(rs.getString("destination"));
                    itineraryDetails.add(rs.getString("eta"));
                }
            }
        }
            if (itineraryDetails.isEmpty()) {
            System.out.println("No itinerary found for " + username + " with travel name: " + travelName);
        }
    
        return itineraryDetails;
    }
    
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            throw e; // Re-throw exception to be handled by the calling method
        }
        return connection;
    }
    public static int getDayId(int itineraryId, int dayNumber) throws SQLException {
        String sql = "SELECT id FROM days WHERE itinerary_id = ? AND day_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itineraryId);
            pstmt.setInt(2, dayNumber);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Day not found for itinerary ID: " + itineraryId + " and day number: " + dayNumber);
                }
            }
        }
    }
       
    public static List<String> getDestinationsByTravelAndDay(String username, String travelName, int dayNumber) throws SQLException {
        List<String> destinations = new ArrayList<>();
        String sql = "SELECT p.destination " +
             "FROM places p " +
             "JOIN days d ON p.day_id = d.id " +
             "JOIN itineraries i ON d.itinerary_id = i.id " +
             "WHERE i.travel_name = ? AND d.day_number = ? " + 
             "AND i.username = ?";

    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, travelName);
            pstmt.setInt(2, dayNumber);
            pstmt.setString(3, username);  // Set username parameter
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(rs.getString("destination"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting destinations: " + e.getMessage()); // Clearer error message
            throw e; // Re-throw the exception for handling elsewhere if needed
        }
        
        return destinations;
    }
    
    public static List<Integer> getDaysByTravelName(String username, String travelName) throws SQLException {
        List<Integer> daysList = new ArrayList<>();
    
        String sql = "SELECT d.day_number FROM days d " +
             "INNER JOIN itineraries i ON d.itinerary_id = i.id " +
             "WHERE i.travel_name = ? AND i.username = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, travelName);
            pstmt.setString(2, username); 
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    daysList.add(rs.getInt("day_number"));
                }
            }
        }
        return daysList;
    }
    public static void updatePhotoDetails(String username, String placeName, String photoPath, int itineraryId, int dayId) throws SQLException {
        String sql = "UPDATE places " +
                 "SET photo = ? " +
                 "WHERE destination = ? AND day_id = ?"; // Filter directly by day_id

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, photoPath);
            pstmt.setString(2, placeName);
            pstmt.setInt(3, dayId); 

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Photo details updated successfully for place: " + placeName);
            } else {
                throw new SQLException("No matching place found in the user's itinerary for the given day.");
            }
        }
    }
    
    
    public static int getItineraryIdByTravelName(String username, String travelName) throws SQLException {
        String sql = "SELECT id FROM itineraries WHERE travel_name = ? AND username = ?"; // Added username check
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, travelName);
            pstmt.setString(2, username); 
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No itinerary found with travel name '" + travelName + "' for user '" + username + "'.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting itinerary ID: " + e.getMessage()); 
            throw e; 
        }
    }
    public static List<String> getDoneTravelNames(String username) {
        List<String> travelNames = new ArrayList<>();
        String sql = "SELECT travel_name FROM itineraries WHERE username = ? AND done = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, 1);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    travelNames.add(rs.getString("travel_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return travelNames;
    }
    public static String getPhotoPath(String username, String placeName, int dayNumber, int itineraryId) throws SQLException {
        String sql = "SELECT photo FROM places " +
                     "WHERE destination = ? AND day_id = ( " +
                     "    SELECT id FROM days " +
                     "    WHERE day_number = ? AND itinerary_id = ( " +
                     "        SELECT id FROM itineraries " +
                     "        WHERE username = ? AND id = ? " +
                     "    )" +
                     ")"; 
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, placeName);
            pstmt.setInt(2, dayNumber);
            pstmt.setString(3, username);
            pstmt.setInt(4, itineraryId);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("photo");
                } else {
                    return null; 
                }
            }
        }
    }
    public static void deleteItineraryById(Connection conn, int itineraryId) throws SQLException {
        String sql = "DELETE FROM itineraries WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itineraryId);
            pstmt.executeUpdate();
        }
    }
    public static void deleteDayById(Connection conn, int dayId) throws SQLException {
        String sql = "DELETE FROM days WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dayId);
            pstmt.executeUpdate();
        }
    }
    public static List<Integer> getDaysByTravelName(int itineraryId) throws SQLException {
        List<Integer> daysList = new ArrayList<>();
        String sql = "SELECT day_number FROM days WHERE itinerary_id = ?"; // Query by itinerary_id, not travel_name
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itineraryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    daysList.add(rs.getInt("day_number"));
                }
            }
        }
        return daysList;
    }
    public static String getEmailByUsername(String username) throws SQLException {
        String sql = "SELECT email FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        }
        return null; 
    }

    public static List<String> getDestinationsByTravelAndDay(int itineraryId, int dayNumber) throws SQLException {
        List<String> destinations = new ArrayList<>();
        String sql = "SELECT p.destination " +
                "FROM places p " +
                "JOIN days d ON p.day_id = d.id " +
                "WHERE d.day_number = ? AND d.itinerary_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dayNumber);
            pstmt.setInt(2, itineraryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(rs.getString("destination"));
                }
            }
        }
        return destinations;
    }
    public static List<String> getDestinationsAndTimeByTravelAndDay(int itineraryId, int dayNumber) throws SQLException {
        List<String> destinations = new ArrayList<>();
        String sql = "SELECT p.destination, p.time " +
                "FROM places p " +
                "JOIN days d ON p.day_id = d.id " +
                "WHERE d.day_number = ? AND d.itinerary_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dayNumber);
            pstmt.setInt(2, itineraryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    destinations.add(rs.getString("destination"));
                    destinations.add(rs.getString("time"));
                }
            }
        }
        return destinations;
    }

    public static void generateItineraryPaper(int itineraryId, String emailReceiveString) throws SQLException {
        List<String> itineraryDetails = DatabaseHelper.getItineraryById(itineraryId);
        List<Integer> days = DatabaseHelper.getDaysByTravelName(itineraryId);
        
            
        StringBuilder itineraryPaper = new StringBuilder();
        itineraryPaper.append("<html><head>");
        itineraryPaper.append("<style>");
        itineraryPaper.append("body { font-family: sans-serif; margin: 20px; justify-content: center; display: flex; }");
        itineraryPaper.append(".mainContainer { width: 520px; border: 1px solid black; padding: 20px; }");
        itineraryPaper.append("h1 { color: #333; text-align: center; background-color:  rgb( 224, 31, 147); padding: 10px; border-radius: 10px; }");
        itineraryPaper.append("h2 { color: #000; }");
        itineraryPaper.append(".day-container { border: 1px solid #ccc; padding: 15px; border-radius: 5px; margin-bottom: 20px; background-color:  rgb(255, 186, 228); }");
        itineraryPaper.append("ul { list-style: none; padding: 0; }");
        itineraryPaper.append("li { margin-bottom: 10px; }");
        itineraryPaper.append("</style>");
        itineraryPaper.append("</head><body>");
        itineraryPaper.append("<div class='mainContainer'>");
        itineraryPaper.append("<h1>").append(itineraryDetails.get(6)).append("'s Itinerary</h1>");
        itineraryPaper.append("<p><strong>Travel Name:</strong> ").append(itineraryDetails.get(0)).append("</p>");
        itineraryPaper.append("<p><strong>Date:</strong> ").append(itineraryDetails.get(1)).append("</p>");
        itineraryPaper.append("<p><strong>Starting Point:</strong> ").append(itineraryDetails.get(2)).append("</p>");
        itineraryPaper.append("<p><strong>ETD (Estimated Time of Departure):</strong> ").append(itineraryDetails.get(3)).append("</p>");
        itineraryPaper.append("<p><strong>Destination Point:</strong> ").append(itineraryDetails.get(4)).append("</p>");
        itineraryPaper.append("<p><strong>ETA (Estimated Time of Arrival):</strong> ").append(itineraryDetails.get(5)).append("</p>");

        for (int dayNumber : days) {
            itineraryPaper.append("<div class='day-container'>");
            itineraryPaper.append("<h2>Day ").append(dayNumber).append(":</h2>");

            List<String> destinations = DatabaseHelper.getDestinationsAndTimeByTravelAndDay(itineraryId, dayNumber);
            itineraryPaper.append("<ul>");
            for (int i = 0; i < destinations.size(); i+=2) {
                itineraryPaper.append("<li>").append(destinations.get(i)).append(" - ").append(destinations.get(i+1)).append("</li>");
            }
            itineraryPaper.append("</ul>");
            itineraryPaper.append("</div>"); 
        }

        itineraryPaper.append("</div>");
        itineraryPaper.append("</body></html>");

        EmailData emailData = new EmailData(itineraryPaper.toString(), emailReceiveString, itineraryId); // Create EmailData object

        Thread emailThread = new Thread(() -> {
            try {
                SentToEMail(emailData);
            } catch (MessagingException | FileNotFoundException | UnsupportedEncodingException e) {
                System.err.println("Error sending email for itinerary " + emailData.itineraryId + ": " + e.getMessage());
            }
        });
        emailThread.start(); 
    }
    public static void SentToEMail(EmailData emailData) throws MessagingException, FileNotFoundException, UnsupportedEncodingException {

        String itineraryHtmlString = emailData.itineraryHtmlString;
        String emailReceiverString = emailData.emailReceiveString;
        int itineraryId = emailData.itineraryId;

        String to = emailReceiverString;
        String from = "jrcabalo056@gmail.com"; 
        String host = "smtp.gmail.com"; 
        String password = "tskc fvyi vflo ldlm"; 

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        try (PrintWriter writer = new PrintWriter("Maven_ParDis/456/src/main/resources/emailTemplate/itinerary.html", "UTF-8")) { 
            writer.print(itineraryHtmlString);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Your Journify Itinerary is Here!");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Please find your Journify itinerary attached.", "plain", "UTF-8");
            multipart.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            String htmlContent = "<html><body style='font-family: sans-serif;'>" +
                "<p>Dear Traveler,</p>" + 
                "<p>Your exciting journey awaits! We've prepared a detailed itinerary for your trip:</p>" +
                emailData.itineraryHtmlString + 
                "<p>Best regards,<br>The Journify Team</p>" +
                "</body></html>";
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            String filename = "Maven_ParDis/456/src/main/resources/emailTemplate/itinerary.html";
            DataSource source = new FileDataSource(filename);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(filename);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email with HTML attachment sent successfully!");

        } catch (MessagingException e) {
        System.err.println("Error sending email for itinerary " + itineraryId + ": " + e.getMessage());
        throw e;
    }
}
    
    public static void main(String[] args) throws SQLException {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.execute(() -> {
                try {
                    String email = DatabaseHelper.getEmailByUsername("Aadinnr"); 
                    if (email != null) {
                        generateItineraryPaper(1, email); 
                    }
                } catch (SQLException e) {
                    System.err.println("Error processing itinerary " + ": " + e.getMessage());
                }
            });
        } finally {
            executor.shutdown(); 
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); 
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
