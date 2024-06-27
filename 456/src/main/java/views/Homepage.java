package views;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;

public class Homepage extends JFrame implements MouseListener, MouseMotionListener {

    private JPanel sidebar;
    private JLabel greetingLabel;
    private int mouseX, mouseY;

    private final int width = 1280;
    private final int height = 720;
    private final int titleFontSize = 50;
    private final int normalFontSize = 20;

    public Homepage(String username) {
        setTitle("Homepage");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(224, 31, 147));
        topBar.addMouseListener(this);
        topBar.addMouseMotionListener(this);
        mainPanel.add(topBar, BorderLayout.NORTH);

        JButton closeButton = new JButton("X");
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(new Color(224, 31, 147));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.addActionListener(e -> System.exit(0)); // Close application on click

        topBar.add(closeButton, BorderLayout.EAST);

        sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new ShadowBorder());

        greetingLabel = new JLabel("Hi Traveler! " + username, SwingConstants.CENTER);
        greetingLabel.setFont(new Font("Arial", Font.BOLD, normalFontSize));
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        greetingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton homepageButton = createSidebarButton("HOMEPAGE");
        
        JButton createItineraryButton = createSidebarButton("CREATE ITINERARY");
        createItineraryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreateItinerary().setVisible(true);
                dispose();
            }
        });
        JButton historyButton = createSidebarButton("HISTORY");
        historyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new History(username).setVisible(true);
                dispose();
            }
        });
        JButton logoutButton = createSidebarButton("LOGOUT");
        logoutButton.addActionListener(e -> logout());

        sidebar.add(greetingLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(homepageButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(createItineraryButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(historyButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        sidebar.add(logoutButton);

        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel travelPlanPanel1 = createTravelPlanPanel("Travel Plan 1");
        JPanel travelPlanPanel2 = createTravelPlanPanel("Travel Plan 2");
        JPanel travelPlanPanel3 = createTravelPlanPanel("Travel Plan 3");

        mainContent.add(travelPlanPanel1);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        mainContent.add(travelPlanPanel2);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        mainContent.add(travelPlanPanel3);

        mainPanel.add(mainContent, BorderLayout.CENTER);

        JButton toggleSidebarButton = new JButton("=");
        toggleSidebarButton.setForeground(Color.BLACK);
        toggleSidebarButton.setBackground(new Color(224, 31, 147));
        toggleSidebarButton.setFocusPainted(false);
        toggleSidebarButton.setBorderPainted(false);
        toggleSidebarButton.setFont(new Font("Arial", Font.BOLD, normalFontSize));
        toggleSidebarButton.addActionListener(e -> toggleSidebar());

        topBar.add(toggleSidebarButton, BorderLayout.WEST);
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
                button.setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createTravelPlanPanel(String planName) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setPreferredSize(new Dimension(500, 100));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(planName, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, normalFontSize));
        panel.add(label, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                travelPlanClicked(planName);
            }
        });

        return panel;
    }

    private void travelPlanClicked(String planName) {
        JOptionPane.showMessageDialog(this, "You clicked on: " + planName, "Travel Plan Clicked", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleSidebar() {
        sidebar.setVisible(!sidebar.isVisible());
        revalidate();
        repaint();
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logged out", "Notification", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true); 
            dispose();
        });
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

    public static void main(String[] args) {
        String username = "";
        SwingUtilities.invokeLater(() -> {
            new Homepage(username).setVisible(true);
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
}
