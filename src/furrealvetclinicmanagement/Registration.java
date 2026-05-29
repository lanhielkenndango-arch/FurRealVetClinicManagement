/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package furrealvetclinicmanagement;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 *
 * @author Asus
 */
public class Registration extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Registration.class.getName());
    private static final Color BRAND_BLUE = new Color(13, 82, 214);
    private static final Color HERO_BLUE = new Color(58, 122, 230);
    private static final Color HERO_LIGHT = new Color(157, 203, 255);
    private static final Color DEEP_BLUE = new Color(12, 31, 66);
    private static final Color MUTED_TEXT = new Color(110, 119, 138);
    private static final Color FIELD_BORDER = new Color(199, 210, 226);
    private static final int WINDOW_WIDTH = 1120;
    private static final int WINDOW_HEIGHT = 720;
    private static final int CARD_WIDTH = 850;
    private static final int CARD_HEIGHT = 610;
    private static final int HERO_WIDTH = 390;
    private static final int FORM_WIDTH = 380;
    private static final int HALF_FIELD_WIDTH = 182;
    private static final int FIELD_HEIGHT = 48;
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Creates new form Login
     */
    public Registration() {
        initComponents();
        arrangeRegistrationLikeReference();
        Back.addActionListener(evt -> openLogin());
        roundedButton1.addActionListener(this::roundedButton1ActionPerformed);
        setupPlaceholderLogic();
        setupEnterKeyNavigation();
        TextFieldFocusUtil.install(getContentPane());
        this.setLocationRelativeTo(null);
    }

    private void openLogin() {
        new Login().setVisible(true);
        dispose();
    }

    private void setupPlaceholderLogic() {
        TextPlaceholderUtil.applyPlaceholder(FirstName1, "First Name");
        TextPlaceholderUtil.applyPlaceholder(LastName, "Last Name");
        TextPlaceholderUtil.applyPlaceholder(PhoneNumber, "Phone Number");
        PhoneNumberUtil.applyPhoneMask(PhoneNumber);
        TextPlaceholderUtil.applyPlaceholder(EmailAdress2, "Email Address");
        TextPlaceholderUtil.applyPlaceholder(Password, "Password");
        TextPlaceholderUtil.applyPlaceholder(ConfirmPass, "Confirm Password");
    }

    private void setupEnterKeyNavigation() {
        FirstName1.addActionListener(evt -> LastName.requestFocusInWindow());
        LastName.addActionListener(evt -> PhoneNumber.requestFocusInWindow());
        PhoneNumber.addActionListener(evt -> EmailAdress2.requestFocusInWindow());
        EmailAdress2.addActionListener(evt -> Password.requestFocusInWindow());
        Password.addActionListener(evt -> ConfirmPass.requestFocusInWindow());
        ConfirmPass.addActionListener(evt -> roundedButton1.doClick());
    }

    private void arrangeRegistrationLikeReference() {
        watermarkPanel1.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        roundedPanel1.setBackground(Color.WHITE);
        roundedPanel1.setCornerRadius(28);

        Back = new studentenrollmentsystem.RoundedButton("Back");
        Back.setIcon(new BackArrowIcon(18));
        Back.setIconTextGap(10);
        configureButton(Back, Color.WHITE, BRAND_BLUE, 98, 42);
        Back.setFont(new Font("Segoe UI", Font.BOLD, 15));

        heroPanel = new HeroPanel();
        heroPanel.setPreferredSize(new Dimension(HERO_WIDTH, CARD_HEIGHT));
        heroPanel.setMinimumSize(new Dimension(HERO_WIDTH, CARD_HEIGHT));
        heroPanel.setMaximumSize(new Dimension(HERO_WIDTH, CARD_HEIGHT));

        FirstName1 = new IconTextField(FieldIcon.USER, false);
        LastName = new IconTextField(FieldIcon.USER, false);
        PhoneNumber = new IconTextField(FieldIcon.PHONE, false);
        EmailAdress2 = new IconTextField(FieldIcon.EMAIL, false);
        Password = new IconTextField(FieldIcon.LOCK, true);
        ConfirmPass = new IconTextField(FieldIcon.LOCK, true);
        configureField(FirstName1, HALF_FIELD_WIDTH);
        configureField(LastName, HALF_FIELD_WIDTH);
        configureField(PhoneNumber, FORM_WIDTH);
        configureField(EmailAdress2, FORM_WIDTH);
        configureField(Password, FORM_WIDTH);
        configureField(ConfirmPass, FORM_WIDTH);

        roundedButton1 = new studentenrollmentsystem.RoundedButton("Register");
        configureButton(roundedButton1, BRAND_BLUE, Color.WHITE, FORM_WIDTH, 50);

        JPanel brandRow = createBrandRow();

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(DEEP_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Fill up the form to get started");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED_TEXT);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JSeparator leftLine = new JSeparator();
        JSeparator rightLine = new JSeparator();
        JLabel orLabel = new JLabel("or");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orLabel.setForeground(MUTED_TEXT);

        JLabel accountPrompt = new JLabel("Already have an account?");
        accountPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountPrompt.setForeground(MUTED_TEXT);
        signInLink = new JLabel("Sign in");
        signInLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signInLink.setForeground(BRAND_BLUE);
        signInLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openLogin();
            }
        });

        JPanel signInRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        signInRow.setOpaque(false);
        signInRow.add(accountPrompt);
        signInRow.add(signInLink);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);

        javax.swing.GroupLayout formLayout = new javax.swing.GroupLayout(formPanel);
        formPanel.setLayout(formLayout);
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(brandRow, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(subtitle, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(formLayout.createSequentialGroup()
                    .addComponent(FirstName1, javax.swing.GroupLayout.PREFERRED_SIZE, HALF_FIELD_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(16, 16, 16)
                    .addComponent(LastName, javax.swing.GroupLayout.PREFERRED_SIZE, HALF_FIELD_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(PhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(EmailAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(ConfirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(roundedButton1, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(formLayout.createSequentialGroup()
                    .addComponent(leftLine, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(14, 14, 14)
                    .addComponent(orLabel)
                    .addGap(14, 14, 14)
                    .addComponent(rightLine, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(signInRow, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        formLayout.setVerticalGroup(
            formLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(brandRow, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(subtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(FirstName1, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LastName, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(PhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(EmailAdress2, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(ConfirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(roundedButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(leftLine, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rightLine, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(signInRow, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout cardLayout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(cardLayout);
        cardLayout.setHorizontalGroup(
            cardLayout.createSequentialGroup()
                .addComponent(heroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, HERO_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
        );
        cardLayout.setVerticalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(heroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, CARD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, CARD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(watermarkPanel1);
        watermarkPanel1.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(backgroundLayout.createSequentialGroup()
                    .addGap(36, 36, 36)
                    .addComponent(Back, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(backgroundLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, CARD_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(backgroundLayout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(Back, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(backgroundLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, CARD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }

    private JPanel createBrandRow() {
        JLabel brandName = new JLabel("FurReal");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brandName.setForeground(BRAND_BLUE);

        JLabel brandSubtitle = new JLabel("Vet Clinic Management");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSubtitle.setForeground(BRAND_BLUE);
        brandSubtitle.setPreferredSize(new Dimension(210, 16));

        JPanel brandCopy = new JPanel();
        brandCopy.setOpaque(false);
        brandCopy.setLayout(new javax.swing.BoxLayout(brandCopy, javax.swing.BoxLayout.Y_AXIS));
        brandName.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandCopy.add(brandName);
        brandCopy.add(brandSubtitle);

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        brandRow.setOpaque(false);
        brandRow.add(new JLabel(new PawHeartIcon(52, BRAND_BLUE, Color.WHITE)));
        brandRow.add(brandCopy);
        return brandRow;
    }

    private void configureField(IconTextField field, int width) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(31, 41, 55));
        field.setCaretColor(BRAND_BLUE);
        field.setPreferredSize(new Dimension(width, FIELD_HEIGHT));
        field.setMinimumSize(new Dimension(width, FIELD_HEIGHT));
        field.setMaximumSize(new Dimension(width, FIELD_HEIGHT));
    }

    private void configureButton(studentenrollmentsystem.RoundedButton button, Color background, Color foreground,
            int width, int height) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
    }

    private enum FieldIcon {
        USER, PHONE, EMAIL, LOCK
    }

    private static class IconTextField extends studentenrollmentsystem.RoundedTextField {
        IconTextField(FieldIcon icon, boolean showEye) {
            setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
            g2.dispose();
        }
    }

    private static class BackArrowIcon implements Icon {
        private final int size;

        BackArrowIcon(int size) {
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BRAND_BLUE);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int mid = y + size / 2;
            g2.drawLine(x + 4, mid, x + size - 3, mid);
            g2.drawLine(x + 4, mid, x + 10, y + 4);
            g2.drawLine(x + 4, mid, x + 10, y + size - 4);
            g2.dispose();
        }
    }

    private static class PawHeartIcon implements Icon {
        private final int size;
        private final Color markColor;
        private final Color heartColor;

        PawHeartIcon(int size, Color markColor, Color heartColor) {
            this.size = size;
            this.markColor = markColor;
            this.heartColor = heartColor;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintMark(g2, x, y, size, markColor, heartColor);
            g2.dispose();
        }

        private static void paintMark(Graphics2D g2, int x, int y, int size, Color markColor, Color heartColor) {
            Graphics2D icon = (Graphics2D) g2.create();
            icon.translate(x, y);
            icon.scale(size / 52.0, size / 52.0);
            icon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            icon.setColor(markColor);
            icon.fill(new Ellipse2D.Double(15, 3, 10, 15));
            icon.fill(new Ellipse2D.Double(27, 3, 10, 15));
            icon.fill(new Ellipse2D.Double(6, 16, 11, 14));
            icon.fill(new Ellipse2D.Double(35, 16, 11, 14));

            Path2D pad = new Path2D.Double();
            pad.moveTo(26, 49);
            pad.curveTo(11, 42, 10, 28, 21, 24);
            pad.curveTo(24, 23, 25, 26, 26, 30);
            pad.curveTo(27, 26, 28, 23, 31, 24);
            pad.curveTo(42, 28, 41, 42, 26, 49);
            pad.closePath();
            icon.fill(pad);

            icon.setColor(heartColor);
            Path2D heart = new Path2D.Double();
            heart.moveTo(26, 40);
            heart.curveTo(18, 34, 19, 28, 24, 29);
            heart.curveTo(25, 29, 26, 30, 26, 31);
            heart.curveTo(26, 30, 27, 29, 28, 29);
            heart.curveTo(33, 28, 34, 34, 26, 40);
            heart.closePath();
            icon.fill(heart);
            icon.dispose();
        }
    }

    private static class HeroPanel extends JPanel {
        private BufferedImage source;

        HeroPanel() {
            setOpaque(false);
            try {
                java.net.URL imgURL = getClass().getResource("/furrealvetclinicmanagement/furry.png");
                if (imgURL != null) {
                    source = ImageIO.read(imgURL);
                }
            } catch (Exception e) {
                source = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int width = getWidth();
            int height = getHeight();

            g2.setPaint(new GradientPaint(0, 0, HERO_BLUE, width, height, HERO_LIGHT));
            g2.fillRoundRect(0, 0, width, height, 28, 28);

            g2.setComposite(AlphaComposite.SrcOver.derive(0.55f));
            drawDotGrid(g2, width - 95, 26, 7, 6);
            drawDotGrid(g2, 20, height - 92, 7, 6);
            g2.setComposite(AlphaComposite.SrcOver);

            drawHeroLogo(g2, 96, 72);
            drawSparkle(g2, 45, 158, 7);
            drawSparkle(g2, 54, 318, 7);
            drawSparkle(g2, width - 28, 250, 10);
            drawSparkle(g2, width - 108, 352, 8);

            drawShadowText(g2, "Compassionate", new Font("Segoe UI", Font.BOLD, 36), 70, 220);
            drawShadowText(g2, "Care, Every Day", new Font("Segoe UI", Font.BOLD, 36), 70, 262);

            g2.setColor(new Color(238, 247, 255, 180));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(94, 296, 186, 296);
            g2.drawLine(226, 296, 318, 296);
            drawSmallHeart(g2, 206, 286);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            g2.setColor(Color.WHITE);
            drawCenteredString(g2, "We're here to help you provide the", width / 2, 330);
            drawCenteredString(g2, "best care for every furry friend.", width / 2, 354);

            if (source != null) {
                drawIllustrationLineArt(g2, source, 810, 230, 760, 650, 40, height - 244, width - 80, 218);
            }
            g2.dispose();
        }

        private void drawHeroLogo(Graphics2D g2, int x, int y) {
            PawHeartIcon.paintMark(g2, x, y, 42, Color.WHITE, new Color(72, 136, 230));
            drawShadowText(g2, "FurReal", new Font("Segoe UI", Font.BOLD, 27), x + 50, y + 30);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(Color.WHITE);
            g2.drawString("Vet Clinic Management", x + 52, y + 47);
        }

        private void drawIllustrationLineArt(Graphics2D g2, BufferedImage source,
                int srcX, int srcY, int srcWidth, int srcHeight,
                int destX, int destY, int destWidth, int destHeight) {
            BufferedImage scaled = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D cropGraphics = scaled.createGraphics();
            cropGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            cropGraphics.drawImage(source, 0, 0, destWidth, destHeight,
                    srcX, srcY, srcX + srcWidth, srcY + srcHeight, null);
            cropGraphics.dispose();

            for (int y = 0; y < scaled.getHeight(); y++) {
                for (int x = 0; x < scaled.getWidth(); x++) {
                    int argb = scaled.getRGB(x, y);
                    int red = (argb >> 16) & 0xff;
                    int green = (argb >> 8) & 0xff;
                    int blue = argb & 0xff;
                    int brightness = (red + green + blue) / 3;

                    if (red < 120 && green < 150 && blue < 205 && brightness < 145) {
                        int alpha = Math.min(230, Math.max(80, (155 - brightness) * 3));
                        scaled.setRGB(x, y, (alpha << 24) | (8 << 16) | (43 << 8) | 105);
                    } else {
                        scaled.setRGB(x, y, 0x00000000);
                    }
                }
            }

            g2.drawImage(scaled, destX, destY, null);
        }

        private void drawShadowText(Graphics2D g2, String text, Font font, int x, int y) {
            g2.setFont(font);
            g2.setColor(new Color(12, 31, 66, 70));
            g2.drawString(text, x + 2, y + 3);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);
        }

        private void drawCenteredString(Graphics2D g2, String text, int centerX, int baselineY) {
            FontMetrics metrics = g2.getFontMetrics();
            g2.drawString(text, centerX - metrics.stringWidth(text) / 2, baselineY);
        }

        private void drawDotGrid(Graphics2D g2, int x, int y, int columns, int rows) {
            g2.setColor(new Color(234, 244, 255));
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    g2.fillOval(x + col * 10, y + row * 10, 3, 3);
                }
            }
        }

        private void drawSparkle(Graphics2D g2, int x, int y, int size) {
            g2.setColor(new Color(238, 247, 255, 210));
            Path2D sparkle = new Path2D.Double();
            sparkle.moveTo(x, y - size);
            sparkle.lineTo(x + size / 3.0, y - size / 3.0);
            sparkle.lineTo(x + size, y);
            sparkle.lineTo(x + size / 3.0, y + size / 3.0);
            sparkle.lineTo(x, y + size);
            sparkle.lineTo(x - size / 3.0, y + size / 3.0);
            sparkle.lineTo(x - size, y);
            sparkle.lineTo(x - size / 3.0, y - size / 3.0);
            sparkle.closePath();
            g2.fill(sparkle);
        }

        private void drawSmallHeart(Graphics2D g2, int x, int y) {
            Path2D heart = new Path2D.Double();
            heart.moveTo(x, y + 15);
            heart.curveTo(x - 15, y + 5, x - 7, y - 5, x, y + 3);
            heart.curveTo(x + 7, y - 5, x + 15, y + 5, x, y + 15);
            heart.closePath();
            g2.fill(heart);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        watermarkPanel1.setImagePath("/furrealvetclinicmanagement/bgl.png");

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 611, Short.MAX_VALUE)
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout watermarkPanel1Layout = new javax.swing.GroupLayout(watermarkPanel1);
        watermarkPanel1.setLayout(watermarkPanel1Layout);
        watermarkPanel1Layout.setHorizontalGroup(
            watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(watermarkPanel1Layout.createSequentialGroup()
                .addGap(154, 154, 154)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(142, Short.MAX_VALUE))
        );
        watermarkPanel1Layout.setVerticalGroup(
            watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(watermarkPanel1Layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
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

    private void roundedButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String firstName = TextPlaceholderUtil.clean(FirstName1);
        String lastName = TextPlaceholderUtil.clean(LastName);
        String phone = PhoneNumberUtil.format(TextPlaceholderUtil.clean(PhoneNumber));
        String email = TextPlaceholderUtil.clean(EmailAdress2);
        String password = TextPlaceholderUtil.clean(Password);
        String confirmPassword = TextPlaceholderUtil.clean(ConfirmPass);

        if (ValidationUtil.hasBlank(firstName, lastName, phone, email, password, confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Please complete all registration fields.");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a Gmail address ending in @gmail.com.");
            return;
        }
        email = email.toLowerCase(Locale.ROOT);

        if (!PhoneNumberUtil.isValidPhilippineMobile(phone)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number using 09##-###-####.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password and confirm password do not match.");
            return;
        }

        if (clientDAO.clientNameExists(firstName, lastName)) {
            JOptionPane.showMessageDialog(this,
                    "A client with the same first name and last name already exists.");
            return;
        }

        Client client = new Client(firstName, lastName, phone, email, password);
        int clientId = clientDAO.addClient(client);
        if (clientId == -1) {
            JOptionPane.showMessageDialog(this, "Registration failed. Email or phone may already be registered.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration successful. Your client ID is " + clientId + ".");
        new Login().setVisible(true);
        dispose();
    }

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
                new Registration().setVisible(true);
            }
        });
        
        /* Create and display the form */
      //  java.awt.EventQueue.invokeLater(() -> new Registration().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private IconTextField ConfirmPass;
    private IconTextField EmailAdress2;
    private IconTextField FirstName1;
    private IconTextField LastName;
    private IconTextField Password;
    private IconTextField PhoneNumber;
    private studentenrollmentsystem.RoundedButton Back;
    private HeroPanel heroPanel;
    private studentenrollmentsystem.RoundedButton roundedButton1;
    private design.RoundedPanel roundedPanel1;
    private JLabel signInLink;
    private design.WatermarkPanel watermarkPanel1;
    // End of variables declaration//GEN-END:variables
}
