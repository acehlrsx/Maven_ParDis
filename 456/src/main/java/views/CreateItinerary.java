package views;
import design.RoundedCornerButton;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DatabaseHelper;

public class CreateItinerary extends JFrame {

    private JTextField calendarField;
    private JTextField travelNameField;
    private JTextField startingLocationField;
    private JTextField etdField;
    private JTextField destinationField;
    private JTextField etaField;

    private JPanel contentPanel;
    private String username = "";
    private int dayCount = 1;
    private JPanel sidebar;
    private JButton addDayButton;
    private JLabel greetingLabel;

    private final int width = 1280;
    private final int height = 720;
    private final int normalFontSize = 20;

    private List<ItineraryDay> itineraryDays = new ArrayList<>();

    private class ItineraryDay {
        String dayDate;
        List<Place> places = new ArrayList<>();
    }

    private class Place {
        String date;
        String destination;
    }

    public CreateItinerary() {
        setTitle("Create Itinerary");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(224, 31, 147));
        mainPanel.add(topBar, BorderLayout.NORTH);

        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(new Color(224, 31, 147));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, normalFontSize)); 
        closeButton.addActionListener(e -> System.exit(0));

        topBar.add(closeButton, BorderLayout.EAST);

        JButton toggleSidebarButton = new JButton("☰");
        toggleSidebarButton.setForeground(Color.BLACK);
        toggleSidebarButton.setBackground(new Color(224, 31, 147));
        toggleSidebarButton.setFocusPainted(false);
        toggleSidebarButton.setBorderPainted(false);
        toggleSidebarButton.setFont(new Font("Arial", Font.BOLD, 18));
        toggleSidebarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sidebar.setVisible(!sidebar.isVisible());
            }
        });

        topBar.add(toggleSidebarButton, BorderLayout.WEST);

        sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new ShadowBorder());

        greetingLabel = new JLabel("Hi Traveler! " + username, SwingConstants.CENTER);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, normalFontSize));
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton homepageButton = createSidebarButton("HOMEPAGE");
        homepageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Homepage(username).setVisible(true);
                dispose();
            }
        });
        JButton createItineraryButton = createSidebarButton("CREATE ITINERARY");

        JButton historyButton = createSidebarButton("HISTORY");
        historyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the Create Itinerary window
                new History(username).setVisible(true);
                dispose(); // Close the current window
            }
        });
        JButton logoutButton = createSidebarButton("LOGOUT");
        logoutButton.addActionListener(e -> logout());

        sidebar.add(greetingLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(homepageButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(createItineraryButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(historyButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(logoutButton);

        mainPanel.add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        addFixedFormPanel();

        addDayPanel();

        addDayButton = new RoundedCornerButton("Add more day/s");
        addDayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addDayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDayPanel();
            }
        });

        createItineraryButton = new RoundedCornerButton("Create Itinerary");
        createItineraryButton.setBackground(new Color(252, 171, 78));
        createItineraryButton.setForeground(Color.BLACK);
        createItineraryButton.setFont(new Font("Arial", Font.BOLD, 15)); // Use updated normal font size
        createItineraryButton.setFocusPainted(false);
        createItineraryButton.setBorderPainted(false);
        createItineraryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createItineraryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createItineraryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DatabaseHelper.createItineraryTable();
                    DatabaseHelper.createDaysTable();
                    DatabaseHelper.createPlacesTable();
                    String travelName = travelNameField.getText().trim(); 
                    String itineraryDate = calendarField.getText().trim();

                    // 2. Input Validation
                    if (travelName.isEmpty() || itineraryDate.isEmpty()) {
                        JOptionPane.showMessageDialog(CreateItinerary.this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }

                    // Validate other fields as needed
                    // (e.g., ensure dates/times are in the correct format, destinations are not empty)

                    String startingLocation = startingLocationField.getText().trim();
                    String etd = etdField.getText().trim();
                    String destination = destinationField.getText().trim();
                    String eta = etaField.getText().trim();

                    // 3. Insert itinerary into the database
                    int itineraryId = DatabaseHelper.insertItinerary(username, travelName, itineraryDate, startingLocation, etd, destination, eta);
                    if (itineraryId == -1) {
                        throw new SQLException("Failed to insert itinerary.");
                    }

                    // 4. Insert days and places for each day
                    for (int i = 0; i < itineraryDays.size(); i++) {
                        ItineraryDay day = itineraryDays.get(i);

                        if (day.dayDate == null || day.dayDate.trim().isEmpty()) {
                            throw new IllegalArgumentException("Day date cannot be empty.");
                        }

                        DatabaseHelper.insertDay(itineraryId, i + 1, day.dayDate.trim());
                        int dayId = DatabaseHelper.getLastInsertId(); 

                        for (Place place : day.places) {
                            DatabaseHelper.insertPlace(dayId, place.date, place.destination);
                        }
                    }

                    // 5. Show success notification and navigate
                    JOptionPane.showMessageDialog(CreateItinerary.this, "Itinerary created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                } catch (SQLException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(CreateItinerary.this, "Error creating itinerary: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Log the error for debugging
                }
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addDayButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0))); // Add space between buttons
        buttonPanel.add(createItineraryButton);


        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addFixedFormPanel() {
        JPanel fixedFormPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        fixedFormPanel.setBackground(Color.WHITE);
        fixedFormPanel.setMaximumSize(new Dimension(1000, 150));
        fixedFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel dateLabel = new JLabel("Date");
        fixedFormPanel.add(dateLabel);
        calendarField = new JTextField("");
        fixedFormPanel.add(calendarField);

        JLabel travelNameLabel = new JLabel("Travel Name");
        fixedFormPanel.add(travelNameLabel);
        travelNameField = new JTextField("");

        fixedFormPanel.add(travelNameField);

        JLabel startingLocationLabel = new JLabel("Starting Location");
        fixedFormPanel.add(startingLocationLabel);
        startingLocationField = new JTextField("");
        fixedFormPanel.add(startingLocationField);

        JLabel etdLabel = new JLabel("ETD");
        fixedFormPanel.add(etdLabel);
        etdField = new JTextField("");
        fixedFormPanel.add(etdField);

        JLabel destinationLabel = new JLabel("Destination");
        fixedFormPanel.add(destinationLabel);
        destinationField = new JTextField("");
        fixedFormPanel.add(destinationField);

        JLabel etaLabel = new JLabel("ETA");
        fixedFormPanel.add(etaLabel);
        etaField = new JTextField("");
        fixedFormPanel.add(etaField);

        Dimension textFieldSize = new Dimension(150, 30);
        calendarField.setPreferredSize(textFieldSize);
        travelNameField.setPreferredSize(textFieldSize);
        startingLocationField.setPreferredSize(textFieldSize);
        etdField.setPreferredSize(textFieldSize);
        destinationField.setPreferredSize(textFieldSize);
        etaField.setPreferredSize(textFieldSize);

        contentPanel.add(fixedFormPanel);
    }

    private void addDayPanel() {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
        dayPanel.setMaximumSize(new Dimension(1000, 200));
        dayPanel.setBackground(Color.WHITE);
        dayPanel.setBorder(BorderFactory.createTitledBorder("Day " + dayCount));

        JPanel placesContainer = new JPanel();
        placesContainer.setLayout(new BoxLayout(placesContainer, BoxLayout.Y_AXIS));
        placesContainer.setBackground(Color.WHITE);

        JScrollPane placesScrollPane = new JScrollPane(placesContainer);
        placesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        placesScrollPane.setPreferredSize(new Dimension(400, 200));

        itineraryDays.add(new ItineraryDay());

        addPlacePanel(placesContainer, dayCount, 1);

        JButton addPlaceButton = new RoundedCornerButton("Add Place");
        addPlaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addPlaceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addPlaceButton.addActionListener(new ActionListener() {
            private int placeCount = 2;

            @Override
            public void actionPerformed(ActionEvent e) {
                addPlacePanel(placesContainer,dayCount, placeCount++);
            }
        });

        dayPanel.add(placesScrollPane);
        dayPanel.add(addPlaceButton);

        contentPanel.add(dayPanel);
        contentPanel.revalidate();
        contentPanel.repaint();

        dayCount++;
    }

    private void addPlacePanel(JPanel placesContainer, int dayNumber, int placeNumber) {
        JPanel placePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        placePanel.setBackground(Color.WHITE);
        placePanel.setBorder(BorderFactory.createTitledBorder("Place " + placeNumber));

        JTextField dateField = new JTextField("");
        dateField.setForeground(Color.GRAY);
        dateField.setText(" Enter Time");
        placePanel.add(dateField);
        dateField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dateField.getText().equals(" Enter Time")) {
                    dateField.setText("");
                    dateField.setForeground(Color.BLACK); // Normal text color
                }
            }
        
            @Override
            public void focusLost(FocusEvent e) {
                if (dateField.getText().isEmpty()) {
                    dateField.setForeground(Color.GRAY);
                    dateField.setText(" Enter Time");
                }
            }
        });

        JTextField destinationField = new JTextField("");
        destinationField.setForeground(Color.GRAY);
        destinationField.setText(" Enter your destination");
        placePanel.add(destinationField);

        destinationField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (destinationField.getText().equals(" Enter your destination")) {
                    destinationField.setText("");
                    destinationField.setForeground(Color.BLACK); // Normal text color
                }
            }
        
            @Override
            public void focusLost(FocusEvent e) {
                if (destinationField.getText().isEmpty()) {
                    destinationField.setForeground(Color.GRAY);
                    destinationField.setText(" Enter your destination");
                }
            }
        });

        Dimension textFieldSize = new Dimension(150, 30);
        dateField.setPreferredSize(textFieldSize);
        destinationField.setPreferredSize(textFieldSize);

        placesContainer.add(placePanel);
        placesContainer.revalidate();
        placesContainer.repaint();

        ItineraryDay currentDay = itineraryDays.get(dayNumber - 1); // Get current day
        currentDay.places.add(new Place());  
        currentDay.places.get(placeNumber - 1).date = dateField.getText();
        currentDay.places.get(placeNumber - 1).destination = destinationField.getText();
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, normalFontSize));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY); // Change to desired hover color
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.WHITE); // Revert to original color
            }
        });

        return button;
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logged out", "Notification", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true); 
            dispose();
        });
    }
    class ShadowBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private final int shadowSize = 5;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color shadowColor = new Color(0, 0, 0, 50);
            for (int i = 0; i < shadowSize; i++) {
                g2d.setColor(shadowColor);
                g2d.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 12, 12);
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = shadowSize;
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CreateItinerary().setVisible(true);
        });
    }

    private Point initialClick;
}