// package design;

// import javax.swing.*;
// import design.RoundedCornerButton;
// import java.awt.*;

// public class RoundedCornerButton extends JButton {

//     private Color backgroundColor;
//     private Color hoverBackgroundColor;
//     private Color pressedBackgroundColor;

//     public RoundedCornerButton(String text) {
//         super(text);
//         setContentAreaFilled(false); // Ensure the content area is not filled by the default mechanism
//         setOpaque(false); // Ensure the component is opaque
//         setFocusPainted(false); // Disable painting of focus indication
//         setBackground(new Color(224, 31, 147)); // Set the default background color
//         setForeground(Color.BLACK); // Set the default foreground color (text color)
//         setFont(new Font("Arial", Font.BOLD, 15)); // Set the default font

//         this.backgroundColor = new Color(224, 31, 147);
//         this.hoverBackgroundColor = new Color(235, 117, 188);
//         this.pressedBackgroundColor = new Color(192, 17, 104);

//         initMouseListener();
//     }

//     private void initMouseListener() {
//         addMouseListener(new java.awt.event.MouseAdapter() {
//             public void mouseEntered(java.awt.event.MouseEvent evt) {
//                 setBackground(hoverBackgroundColor);
//             }

//             public void mouseExited(java.awt.event.MouseEvent evt) {
//                 setBackground(backgroundColor);
//             }

//             public void mousePressed(java.awt.event.MouseEvent evt) {
//                 setBackground(pressedBackgroundColor);
//             }

//             public void mouseReleased(java.awt.event.MouseEvent evt) {
//                 setBackground(hoverBackgroundColor);
//             }
//         });
//     }

//     @Override
//     protected void paintComponent(Graphics g) {
//         Graphics2D g2 = (Graphics2D) g.create();
//         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//         if (getModel().isPressed()) {
//             g2.setColor(pressedBackgroundColor);
//         } else if (getModel().isRollover()) {
//             g2.setColor(hoverBackgroundColor);
//         } else {
//             g2.setColor(backgroundColor);
//         }

//         int arc = getHeight(); // Adjust the arc size to make the button fully rounded
//         g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

//         g2.dispose();

//         super.paintComponent(g);
//     }

//     @Override
//     protected void paintBorder(Graphics g) {
//         // Do not paint the border by default
//     }

//     @Override
//     public Dimension getPreferredSize() {
//         return new Dimension(200, 40); // Set preferred size as needed
//     }

//     @Override
//     public Dimension getMinimumSize() {
//         return new Dimension(200, 40); // Set minimum size as needed
//     }
// }
