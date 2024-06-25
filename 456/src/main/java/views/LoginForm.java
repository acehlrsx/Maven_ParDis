package views;

import model.DatabaseHelper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginForm extends JFrame implements MouseListener, MouseMotionListener {

    private int mouseX, mouseY;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int height = 720, width = 1280, normal_font = 20, title_font = 50;

    public LoginForm() {
        // Set frame properties
        setTitle("Login");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 20);
        Dimension textFieldSize = new Dimension(300, 30);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(224, 31, 147));
        setContentPane(mainPanel);

        // Create top bar panel
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(224, 31, 147));
        topBar.addMouseListener(this);
        topBar.addMouseMotionListener(this);

        // Create close button
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(new Color(224, 31, 147));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> System.exit(0));

        // Add close button to top bar
        topBar.add(closeButton, BorderLayout.EAST);

        // Add top bar to main panel
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Create panels for left and right sides
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(width/2, height));
        ImageIcon imageIcon = new ImageIcon("456/resources/images/journifylogo.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(imgLabel, BorderLayout.CENTER);

        // Create right panel
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(width/2, height));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components for right panel
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, title_font));
        loginLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(loginLabel, gbc);

        JLabel welcomeLabel = new JLabel("Hello! Let's get started");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, normal_font));
        welcomeLabel.setForeground(Color.BLACK);
        gbc.gridy++;
        rightPanel.add(welcomeLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, normal_font));
        usernameLabel.setForeground(Color.BLACK);
        gbc.gridy++;
        gbc.gridwidth = 1;
        rightPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(textFieldSize);
        usernameField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, normal_font));
        passwordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(textFieldSize);
        passwordField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);

        // Show Password checkbox
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setForeground(Color.BLACK);
        showPasswordCheckBox.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        rightPanel.add(showPasswordCheckBox, gbc);

        // Event listener for showPasswordCheckBox
        showPasswordCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    passwordField.setEchoChar((char) 0); // Show the password
                } else {
                    passwordField.setEchoChar('*'); // Hide the password
                }
            }
        });

        JButton loginButton = new JButton("LOGIN");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(224, 31, 147));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy++;
        rightPanel.add(loginButton, gbc);

        // Action listener for LOGIN button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword()); // Get password text

                try {
                    // Authenticate using the DatabaseHelper
                    boolean loginSuccess = DatabaseHelper.authenticateUser(username, password);

                    if (loginSuccess) {
                        JOptionPane.showMessageDialog(LoginForm.this, "Login Successful!");
                        openHomepage(username);
                    } else {
                        JOptionPane.showMessageDialog(LoginForm.this, 
                            "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText(""); 
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace(); // Log the error for debugging
                    JOptionPane.showMessageDialog(LoginForm.this, "An error occurred during login.");
                }
            }
        });

        // Sign Up Label as clickable link
        JLabel signUpLabel = new JLabel("Don't have an account? Sign Up");
        signUpLabel.setForeground(new Color(252, 171, 78));
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open sign up form or dialog
                openSignUpForm();
            }
        });
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(signUpLabel, gbc);

        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
    }

    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // MouseMotionListener method
    @Override
    public void mouseDragged(MouseEvent e) {
        int newX = e.getXOnScreen() - mouseX;
        int newY = e.getYOnScreen() - mouseY;
        setLocation(newX, newY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    // Method to open sign up form or dialog
    private void openSignUpForm() {
        // Replace with your sign up form creation logic
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setVisible(true);
        this.setVisible(false); // Hide login form if needed
    }

    // Method to open homepage
    private void openHomepage(String username) {
        Homepage homepage = new Homepage(username);
        homepage.setVisible(true);
        this.setVisible(false); // Hide login form if needed
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
