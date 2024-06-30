package design;

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
    private JTextField destinationPlaceField;
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

    public CreateItinerary(String username) {
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

        JButton toggleSidebarButton = new JButton("=");
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
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(createItineraryButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(historyButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
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
        createItineraryButton.setFont(new Font("Arial", Font.BOLD, 15));
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

                    if (travelName.isEmpty() || itineraryDate.isEmpty()) {
                        JOptionPane.showMessageDialog(CreateItinerary.this, "Please fill in all required fields.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String startingLocation = startingLocationField.getText().trim();
                    String etd = etdField.getText().trim();
                    String destination = destinationPlaceField.getText().trim();
                    String eta = etaField.getText().trim();

                    int itineraryId = DatabaseHelper.insertItinerary(username, travelName, itineraryDate,
                            startingLocation, etd, destination, eta);
                    if (itineraryId == -1) {
                        throw new SQLException("Failed to insert itinerary.");
                    }
                    Component[] components = contentPanel.getComponents();
                    System.out.println(components.length - 1);
                    for (int i = 1; i < components.length; i++) {
                        Component dayPanelComp = components[i];

                        int dayNumber = i;
                        System.out.println("dayNumber" + dayNumber);
                        DatabaseHelper.insertDay(itineraryId, dayNumber);
                        int dayId = DatabaseHelper.getLastInsertId();

                        if (dayPanelComp instanceof JPanel && dayPanelComp.getName().startsWith("dayPanel")) {
                            JPanel dayPanel = (JPanel) dayPanelComp;

                            JPanel placesContainer = (JPanel) ((JScrollPane) dayPanel.getComponent(0)).getViewport()
                                    .getView();
                            int numPlacePanels = placesContainer.getComponentCount();

                            System.out.println("place" + numPlacePanels);

                            for (int placePanelIndex = 0; placePanelIndex < numPlacePanels; placePanelIndex++) {
                                JPanel placePanel = (JPanel) placesContainer.getComponent(placePanelIndex);

                                JTextField dateField = (JTextField) placePanel.getComponent(0);
                                JTextField destinationField = (JTextField) placePanel.getComponent(1);

                                System.out.println(dateField.getText());
                                System.out.println(destinationField.getText());

                                String placeTime = dateField.getText().trim();
                                String placeDestination = destinationField.getText().trim();

                                // Handle placeholder text for empty fields
                                if (placeTime.equals(" Enter Time")) {
                                    placeTime = ""; // Or set a default value
                                }
                                if (placeDestination.equals(" Enter your destination")) {
                                    placeDestination = "";
                                }
                                DatabaseHelper.insertPlace(dayId, placeTime, placeDestination, null);
                            }
                        }
                    }

                } catch (SQLException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(CreateItinerary.this, "Error creating itinerary: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Log the error for debugging
                }

            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addDayButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(createItineraryButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private boolean areFieldsEmpty() {
        boolean isEmpty = false;
        StringBuilder emptyFields = new StringBuilder("Please fill in the following fields:\n");

        if (calendarField.getText().trim().isEmpty()) {
            isEmpty = true;
            emptyFields.append("- Date\n");
        }
        if (travelNameField.getText().trim().isEmpty()) {
            isEmpty = true;
            emptyFields.append("- Travel Name\n");
        }
        if (startingLocationField.getText().trim().isEmpty()) {
            isEmpty = true;
            emptyFields.append("- Starting Location\n");
        }
        if (etdField.getText().trim().isEmpty()) {
            isEmpty = true;
            emptyFields.append("- ETD\n");
        }
        if (destinationPlaceField.getText().trim().isEmpty()) {
            isEmpty = true;
            emptyFields.append("- Destination\n");
        }
        if (etaField.getText().trim().isEmpty()) {
            isEmpty = true;
            emptyFields.append("- ETA\n");
        }

        // Check additional fields in day panels
        Component[] components = contentPanel.getComponents();
        for (int i = 1; i < components.length; i++) {
            Component dayPanelComp = components[i];
            if (dayPanelComp instanceof JPanel && dayPanelComp.getName().startsWith("dayPanel")) {
                JPanel dayPanel = (JPanel) dayPanelComp;
                JPanel placesContainer = (JPanel) ((JScrollPane) dayPanel.getComponent(0)).getViewport().getView();
                int numPlacePanels = placesContainer.getComponentCount();

                for (int placePanelIndex = 0; placePanelIndex < numPlacePanels; placePanelIndex++) {
                    JPanel placePanel = (JPanel) placesContainer.getComponent(placePanelIndex);
                    JTextField dateField = (JTextField) placePanel.getComponent(0);
                    JTextField destinationField = (JTextField) placePanel.getComponent(1);

                    if (dateField.getText().trim().isEmpty() || dateField.getText().equals(" Enter Time")) {
                        isEmpty = true;
                        emptyFields.append("- Day ").append(i).append(" Place ").append(placePanelIndex + 1)
                                .append(" Time\n");
                    }
                    if (destinationField.getText().trim().isEmpty()
                            || destinationField.getText().equals(" Enter your destination")) {
                        isEmpty = true;
                        emptyFields.append("- Day ").append(i).append(" Place ").append(placePanelIndex + 1)
                                .append(" Destination\n");
                    }
                }
            }
        }

        if (isEmpty) {
            JOptionPane.showMessageDialog(this, emptyFields.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return isEmpty;
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
        destinationPlaceField = new JTextField("");
        fixedFormPanel.add(destinationPlaceField);

        JLabel etaLabel = new JLabel("ETA");
        fixedFormPanel.add(etaLabel);
        etaField = new JTextField("");
        fixedFormPanel.add(etaField);

        Dimension textFieldSize = new Dimension(150, 30);
        calendarField.setPreferredSize(textFieldSize);
        travelNameField.setPreferredSize(textFieldSize);
        startingLocationField.setPreferredSize(textFieldSize);
        etdField.setPreferredSize(textFieldSize);
        destinationPlaceField.setPreferredSize(textFieldSize);
        etaField.setPreferredSize(textFieldSize);

        contentPanel.add(fixedFormPanel);
    }

    private void addDayPanel() {
        JPanel dayPanel = new JPanel();
        dayPanel.setName("dayPanel" + dayCount);
        dayPanel.setLayout(new BorderLayout());
        dayPanel.setMaximumSize(new Dimension(1000, 200));
        dayPanel.setBackground(Color.WHITE);
        dayPanel.setBorder(BorderFactory.createTitledBorder("Day " + dayCount));
    
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(Color.WHITE);
    
        JRadioButton addModeButton = new JRadioButton("Add Place");
        JRadioButton deleteModeButton = new JRadioButton("Delete Day");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(addModeButton);
        modeGroup.add(deleteModeButton);
    
        addModeButton.setSelected(true); // Default mode is to add places
    
        addModeButton.addActionListener(e -> {
            deleteModeButton.setSelected(false);
        });
    
        deleteModeButton.addActionListener(e -> {
            addModeButton.setSelected(false);
        });
    
        buttonsPanel.add(addModeButton);
        buttonsPanel.add(deleteModeButton);
    
        dayPanel.add(buttonsPanel, BorderLayout.NORTH);
    
        JPanel placesContainer = new JPanel();
        placesContainer.setLayout(new BoxLayout(placesContainer, BoxLayout.Y_AXIS));
        placesContainer.setBackground(Color.WHITE);
    
        JScrollPane placesScrollPane = new JScrollPane(placesContainer);
        placesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        placesScrollPane.setPreferredSize(new Dimension(400, 150));
    
        dayPanel.add(placesScrollPane, BorderLayout.CENTER);
    
        contentPanel.add(dayPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    
        dayCount++;
    
        addModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlacePanel(dayPanel, placesContainer, 1);
            }
        });
    
        deleteModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.remove(dayPanel);
                contentPanel.revalidate();
                contentPanel.repaint();
                dayCount--;
            }
        });
    }
    
    private void addPlacePanel(JPanel dayPanel, JPanel placesContainer, int placeNumber) {
        JPanel placePanel = new JPanel(new BorderLayout(10, 10));
        placePanel.setBackground(Color.WHITE);
        placePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    
        JTextField dateField = new JTextField(" Enter Time");
        dateField.setForeground(Color.GRAY);
        dateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dateField.getText().equals(" Enter Time")) {
                    dateField.setText("");
                    dateField.setForeground(Color.BLACK);
                }
            }
    
            @Override
            public void focusLost(FocusEvent e) {
                if (dateField.getText().isEmpty()) {
                    dateField.setText(" Enter Time");
                    dateField.setForeground(Color.GRAY);
                }
            }
        });
    
        JTextField destinationField = new JTextField(" Enter your destination");
        destinationField.setForeground(Color.GRAY);
        destinationField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (destinationField.getText().equals(" Enter your destination")) {
                    destinationField.setText("");
                    destinationField.setForeground(Color.BLACK);
                }
            }
    
            @Override
            public void focusLost(FocusEvent e) {
                if (destinationField.getText().isEmpty()) {
                    destinationField.setText(" Enter your destination");
                    destinationField.setForeground(Color.GRAY);
                }
            }
        });
    
        JButton deletePlaceButton = new RoundedCornerButton("Delete");
        deletePlaceButton.setForeground(Color.BLACK);
        deletePlaceButton.setBackground(new Color(224, 31, 147));
        deletePlaceButton.setFont(new Font("Arial", Font.BOLD, 12));
        deletePlaceButton.setFocusPainted(false);
        deletePlaceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deletePlaceButton.setPreferredSize(new Dimension(0, 30));
        deletePlaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placesContainer.remove(placePanel); // Remove the place panel from its parent
                placesContainer.revalidate();
                placesContainer.repaint();
            }
        });
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(deletePlaceButton);
    
        placePanel.add(dateField, BorderLayout.WEST);
        placePanel.add(destinationField, BorderLayout.CENTER);
        placePanel.add(buttonPanel, BorderLayout.EAST);
    
        Dimension textFieldSize = new Dimension(150, 30);
        dateField.setPreferredSize(textFieldSize);
        destinationField.setPreferredSize(textFieldSize);
    
        placesContainer.add(placePanel);
        placesContainer.revalidate();
        placesContainer.repaint();
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
        String username = "";
        SwingUtilities.invokeLater(() -> {
            new CreateItinerary(username).setVisible(true);
        });
    }

    private Point initialClick;
}