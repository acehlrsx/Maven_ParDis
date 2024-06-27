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
        setTitle("Login");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 20);
        Dimension textFieldSize = new Dimension(300, 30);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(224, 31, 147));
        setContentPane(mainPanel);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(224, 31, 147));
        topBar.addMouseListener(this);
        topBar.addMouseMotionListener(this);

        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(new Color(224, 31, 147));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> System.exit(0));

        topBar.add(closeButton, BorderLayout.EAST);

        mainPanel.add(topBar, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(width/2, height));
        ImageIcon imageIcon = new ImageIcon("Maven_ParDis/456/src/main/resources/images/journifylogo.png");
        Image scaledImage = imageIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(imgLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(width/2, height));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
                } else {
                    passwordField.setEchoChar('*');
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

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
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
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginForm.this, "An error occurred during login.");
                }
            }
        });

        JLabel signUpLabel = new JLabel("Don't have an account? Sign Up");
        signUpLabel.setForeground(new Color(252, 171, 78));
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openSignUpForm();
            }
        });
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(signUpLabel, gbc);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
    }

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

    @Override
    public void mouseDragged(MouseEvent e) {
        int newX = e.getXOnScreen() - mouseX;
        int newY = e.getYOnScreen() - mouseY;
        setLocation(newX, newY);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void openSignUpForm() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setVisible(true);
        this.setVisible(false);
    }

    private void openHomepage(String username) {
        Homepage homepage = new Homepage(username);
        homepage.setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
