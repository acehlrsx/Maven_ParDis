package views;

import javax.swing.*;
import model.DatabaseHelper;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class SignUpForm extends JFrame implements MouseListener, MouseMotionListener {

    private int mouseX, mouseY;
    private int height = 720, width = 1280, normal_font = 20, title_font = 50;

    public SignUpForm() {
        // Set frame properties
        setTitle("Sign Up");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(224, 31, 147));
        setContentPane(mainPanel);

        // Create top bar panel (similar to Login form)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(224, 31, 147));
        topBar.addMouseListener(this);
        topBar.addMouseMotionListener(this);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Close button (similar to Login form)
        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(new Color(224, 31, 147));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> System.exit(0));
        topBar.add(closeButton, BorderLayout.EAST);

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

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(width / 2, height)); // Set preferred size

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Define common fonts
        Font titleFont = new Font("Segoe UI", Font.BOLD, title_font);
        Font normalFont = new Font("Segoe UI", Font.PLAIN, normal_font);
        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 20); // Define font for text fields

        // Define common dimension for text fields
        Dimension textFieldSize = new Dimension(300, 30);

        // Components for right panel
        JLabel signUpLabel = new JLabel("Sign Up");
        signUpLabel.setFont(titleFont);
        signUpLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(signUpLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(normalFont);
        usernameLabel.setForeground(Color.BLACK);
        gbc.gridy++;
        gbc.gridwidth = 1;
        rightPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(textFieldSize);
        usernameField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(normalFont);
        emailLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        emailField.setPreferredSize(textFieldSize);
        emailField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(emailField, gbc);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(normalFont);
        nameLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        nameField.setPreferredSize(textFieldSize);
        nameField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(nameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(normalFont);
        passwordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(textFieldSize);
        passwordField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);

        JLabel reenterPasswordLabel = new JLabel("Re-enter Password");
        reenterPasswordLabel.setFont(normalFont);
        reenterPasswordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(reenterPasswordLabel, gbc);

        JPasswordField reenterPasswordField = new JPasswordField(20);
        reenterPasswordField.setPreferredSize(textFieldSize);
        reenterPasswordField.setFont(textFieldFont);
        gbc.gridx = 1;
        rightPanel.add(reenterPasswordField, gbc);

        // Show Password checkbox
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setForeground(Color.BLACK);
        showPasswordCheckBox.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        rightPanel.add(showPasswordCheckBox, gbc);
        showPasswordCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    passwordField.setEchoChar((char) 0);
                    reenterPasswordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                    reenterPasswordField.setEchoChar('*');
                }
            }
        });

        JButton signUpButton = new JButton("SIGN UP");
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(224, 31, 147));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy++;
        rightPanel.add(signUpButton, gbc);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String name = nameField.getText().trim();
                String password1 = new String(passwordField.getPassword());
                String password2 = new String(reenterPasswordField.getPassword());

                if (password1.equals(password2)) {
                try {
                    DatabaseHelper.createTable();
                    boolean success = DatabaseHelper.insertUser(username, email, name, password1);
                    if(success){
                        JOptionPane.showMessageDialog(SignUpForm.this, "Sign Up Successful!");
                        openLoginForm(); 
                    }else{
                        JOptionPane.showMessageDialog(SignUpForm.this, "Username already exists."); 
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(SignUpForm.this, "An error occurred during sign up.");
                }
            } else {
                    JOptionPane.showMessageDialog(SignUpForm.this, "Passwords do not match. Please try again.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Back to Login Label as clickable link
        JLabel backToLoginLabel = new JLabel("Back to Login");
        backToLoginLabel.setForeground(new Color(252, 171, 78));
        backToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open login form or dialog
                openLoginForm();
            }
        });
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(backToLoginLabel, gbc);

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

    // Method to open login form or dialog
    private void openLoginForm() {
        // Replace with your login form creation logic
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
        this.setVisible(false); // Hide sign up form if needed
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SignUpForm().setVisible(true);
        });
    }
}
