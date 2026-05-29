/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package furrealvetclinicmanagement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/**
 *
 * @author Asus
 */
public class Dashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();

        setHighQualityImage();
        cpaw.addActionListener(evt -> openClientPet());
        circularButton2.addActionListener(evt -> openVisitAndTransaction());
        circularButton4.addActionListener(evt -> openServices());
        LogOut.addActionListener(evt -> logOut());

        this.setLocationRelativeTo(null);
    }

    private void openClientPet() {
        new ClientPet().setVisible(true);
        dispose();
    }

    private void openVisitAndTransaction() {
        new VisitAndTransaction().setVisible(true);
        dispose();
    }

    private void openServices() {
        new Services().setVisible(true);
        dispose();
    }

    private void logOut() {
        new Login().setVisible(true);
        dispose();
    }

    private void setHighQualityImage() {
        cpaw.setIcon(new SmoothPawIcon(95, 95));
        circularButton2.setIcon(new SmoothCalendarIcon(95, 95));
        circularButton4.setIcon(new SmoothSyringeIcon(95, 95));

        cpaw.revalidate();
        cpaw.repaint();
        circularButton2.revalidate();
        circularButton2.repaint();
        circularButton4.revalidate();
        circularButton4.repaint();
    }

    private abstract static class SmoothBadgeIcon implements Icon {
        private final int width;
        private final int height;

        SmoothBadgeIcon(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            double size = Math.min(width, height);
            double scale = size / 95.0;
            g2.translate(x + (width - size) / 2.0, y + (height - size) / 2.0);
            g2.scale(scale, scale);

            paintBadge(g2);
            g2.dispose();
        }

        protected abstract void paintBadge(Graphics2D g2);
    }

    private static class SmoothPawIcon extends SmoothBadgeIcon {
        SmoothPawIcon(int width, int height) {
            super(width, height);
        }

        @Override
        protected void paintBadge(Graphics2D g2) {
            g2.setColor(new Color(235, 224, 255));
            g2.fill(new Ellipse2D.Double(0, 0, 95, 95));

            g2.setColor(new Color(101, 63, 184));
            fillRotatedOval(g2, 20, 34, 14, 18, -24);
            fillRotatedOval(g2, 35, 20, 15, 22, -6);
            fillRotatedOval(g2, 53, 20, 15, 22, 6);
            fillRotatedOval(g2, 67, 34, 14, 18, 24);

            Path2D pad = new Path2D.Double();
            pad.moveTo(47.5, 45);
            pad.curveTo(55, 45, 59, 54, 64, 61);
            pad.curveTo(71, 72, 64, 80, 52, 77);
            pad.curveTo(49, 76, 46, 76, 43, 77);
            pad.curveTo(31, 80, 24, 72, 31, 61);
            pad.curveTo(36, 54, 40, 45, 47.5, 45);
            pad.closePath();
            g2.fill(pad);

            g2.setColor(Color.WHITE);
            Path2D heart = new Path2D.Double();
            heart.moveTo(47.5, 67);
            heart.curveTo(37, 59, 40, 52, 46, 56);
            heart.curveTo(48, 58, 47, 58, 49, 56);
            heart.curveTo(55, 52, 58, 59, 47.5, 67);
            heart.closePath();
            g2.fill(heart);
        }

        private void fillRotatedOval(Graphics2D g2, double x, double y, double width, double height, double degrees) {
            AffineTransform oldTransform = g2.getTransform();
            g2.rotate(Math.toRadians(degrees), x + width / 2.0, y + height / 2.0);
            g2.fill(new Ellipse2D.Double(x, y, width, height));
            g2.setTransform(oldTransform);
        }
    }

    private static class SmoothSyringeIcon extends SmoothBadgeIcon {
        SmoothSyringeIcon(int width, int height) {
            super(width, height);
        }

        @Override
        protected void paintBadge(Graphics2D g2) {
            g2.setColor(new Color(226, 241, 255));
            g2.fill(new Ellipse2D.Double(0, 0, 95, 95));

            AffineTransform oldTransform = g2.getTransform();
            g2.translate(47.5, 47.5);
            g2.rotate(Math.toRadians(-38));

            g2.setColor(new Color(37, 99, 235));
            g2.setStroke(new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(-34, 0, -23, 0);
            g2.drawLine(-23, -8, -16, 8);
            g2.draw(new RoundRectangle2D.Double(-16, -10, 34, 20, 4, 4));
            g2.drawLine(18, 0, 29, 0);
            g2.drawLine(29, -10, 29, 10);
            g2.drawLine(36, -6, 36, 6);

            g2.setStroke(new BasicStroke(2.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(-6, -9, -6, -3);
            g2.drawLine(3, -9, 3, -2);
            g2.drawLine(12, -9, 12, -3);

            g2.setTransform(oldTransform);
        }
    }

    private static class SmoothCalendarIcon extends SmoothBadgeIcon {
        SmoothCalendarIcon(int width, int height) {
            super(width, height);
        }

        @Override
        protected void paintBadge(Graphics2D g2) {
            g2.setColor(new Color(255, 238, 228));
            g2.fill(new Ellipse2D.Double(0, 0, 95, 95));

            g2.setColor(new Color(249, 115, 22));
            g2.setStroke(new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new RoundRectangle2D.Double(27, 25, 40, 39, 5, 5));
            g2.drawLine(27, 37, 67, 37);
            g2.drawLine(38, 19, 38, 30);
            g2.drawLine(56, 19, 56, 30);

            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            drawDot(g2, 39, 47);
            drawDot(g2, 50, 47);
            drawDot(g2, 39, 58);
            drawDot(g2, 50, 58);

            g2.setColor(new Color(255, 238, 228));
            g2.fill(new Ellipse2D.Double(57, 53, 28, 28));
            g2.setColor(new Color(249, 115, 22));
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Double(60, 56, 24, 24));
            g2.drawLine(72, 68, 72, 62);
            g2.drawLine(72, 68, 78, 71);
        }

        private void drawDot(Graphics2D g2, double x, double y) {
            g2.fill(new Ellipse2D.Double(x - 1.5, y - 1.5, 3, 3));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        watermarkPanel1 = new design.WatermarkPanel();
        roundedPanel1 = new design.RoundedPanel();
        cpaw = new design.CircularButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        roundedPanel2 = new design.RoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        circularButton2 = new design.CircularButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        roundedPanel3 = new design.RoundedPanel();
        jLabel5 = new javax.swing.JLabel();
        circularButton4 = new design.CircularButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        LogOut = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        watermarkPanel1.setImagePath("/furrealvetclinicmanagement/dashyb.png");
        watermarkPanel1.setMaximumSize(new java.awt.Dimension(900, 600));
        watermarkPanel1.setMinimumSize(new java.awt.Dimension(900, 600));

        cpaw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/furrealvetclinicmanagement/dpaw.png"))); // NOI18N
        cpaw.setMaximumSize(new java.awt.Dimension(95, 95));
        cpaw.setMinimumSize(new java.awt.Dimension(95, 95));
        cpaw.setPreferredSize(new java.awt.Dimension(95, 95));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(16, 24, 40));
        jLabel1.setText("Manage Clients & Pets");

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 112, 133));
        jLabel2.setText("View and manage client");

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 112, 133));
        jLabel3.setText("and pet information");

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundedPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel2))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel3))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(cpaw, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(cpaw, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(16, 24, 40));
        jLabel4.setText("New Visit / Appointment");

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 112, 133));
        jLabel6.setText("Schedule a new visit or");

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 112, 133));
        jLabel7.setText("appointment");

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(32, 32, 32))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                            .addComponent(circularButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(60, 60, 60)))))
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(circularButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(32, 32, 32))
        );

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(16, 24, 40));
        jLabel5.setText("      Servive Catalog");

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 112, 133));
        jLabel8.setText("Browse and manage");

        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 112, 133));
        jLabel9.setText("clinic services");

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel3Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel3Layout.createSequentialGroup()
                        .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(roundedPanel3Layout.createSequentialGroup()
                                .addComponent(circularButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)))
                        .addGap(44, 44, 44))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(circularButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        LogOut.setBackground(new java.awt.Color(1, 60, 156));
        LogOut.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        LogOut.setForeground(new java.awt.Color(255, 255, 255));
        LogOut.setText("Log Out");
        LogOut.setBorderPainted(false);
        LogOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LogOut.setFocusPainted(false);

        javax.swing.GroupLayout watermarkPanel1Layout = new javax.swing.GroupLayout(watermarkPanel1);
        watermarkPanel1.setLayout(watermarkPanel1Layout);
        watermarkPanel1Layout.setHorizontalGroup(
            watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(watermarkPanel1Layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(roundedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watermarkPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LogOut)
                .addGap(19, 19, 19))
        );
        watermarkPanel1Layout.setVerticalGroup(
            watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, watermarkPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LogOut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addGroup(watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundedPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(142, 142, 142))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(watermarkPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(watermarkPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Activates FlatLaf immediately 
        com.formdev.flatlaf.FlatLightLaf.setup();

        /* ... NetBeans auto-generated code ... */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });

        /* Create and display the form */
        //    java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton LogOut;
    private design.CircularButton circularButton2;
    private design.CircularButton circularButton4;
    private design.CircularButton cpaw;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private design.RoundedPanel roundedPanel1;
    private design.RoundedPanel roundedPanel2;
    private design.RoundedPanel roundedPanel3;
    private design.WatermarkPanel watermarkPanel1;
    // End of variables declaration//GEN-END:variables
}
