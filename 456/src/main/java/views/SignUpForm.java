package views;

import design.RoundedBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpForm extends JFrame implements MouseListener, MouseMotionListener {

    private int mouseX, mouseY;

    public SignUpForm() {
        // Set frame properties
        setTitle("Sign Up");
        setSize(900, 500);
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
        leftPanel.setPreferredSize(new Dimension(450, 500)); // Adjusted width
        ImageIcon imageIcon = new ImageIcon("images\\journifylogo.png"); // Placeholder image path
        Image scaledImage = imageIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(imgLabel, BorderLayout.CENTER);

        // Create right panel
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(600, 500)); // Adjusted width
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components for right panel
        JLabel signUpLabel = new JLabel("Sign Up");
        signUpLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        signUpLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(signUpLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.BLACK);
        gbc.gridy++;
        gbc.gridwidth = 1;
        rightPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        rightPanel.add(emailField, gbc);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        rightPanel.add(nameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);

        JLabel reenterPasswordLabel = new JLabel("Re-enter Password");
        reenterPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reenterPasswordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy++;
        rightPanel.add(reenterPasswordLabel, gbc);

        JPasswordField reenterPasswordField = new JPasswordField(20);
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

        // Event listener for showPasswordCheckBox
        showPasswordCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    passwordField.setEchoChar((char) 0); // Show the password
                    reenterPasswordField.setEchoChar((char) 0); // Show the password
                } else {
                    passwordField.setEchoChar('*'); // Hide the password
                    reenterPasswordField.setEchoChar('*'); // Hide the password
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

        // Action listener for SIGN UP button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password1 = new String(passwordField.getPassword());
                String password2 = new String(reenterPasswordField.getPassword());

                if (password1.equals(password2)) {
                    JOptionPane.showMessageDialog(SignUpForm.this, "Sign Up Successful!");
                    // Navigate back to login form
                    openLoginForm();
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
