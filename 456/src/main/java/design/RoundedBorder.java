// package design;

// import javax.swing.border.AbstractBorder;
// import java.awt.*;
// import java.awt.geom.Area;
// import java.awt.geom.RoundRectangle2D;

// public class RoundedBorder extends AbstractBorder {
//     private int radius;
//     private Color color;

//     public RoundedBorder(int radius, Color color) {
//         this.radius = radius;
//         this.color = color;
//     }

//     @Override
//     public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
//         Graphics2D g2d = (Graphics2D) g.create();
//         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//         Shape roundRect = new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius);
//         Shape rect = new Rectangle(x, y, width, height);

//         Area area = new Area(roundRect);
//         area.subtract(new Area(rect));

//         g2d.setColor(color);
//         g2d.fill(area);

//         g2d.dispose();
//     }

//     @Override
//     public Insets getBorderInsets(Component c) {
//         return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
//     }
// }
