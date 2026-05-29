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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Asus
 */
public class Login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());
    private static final Color BRAND_BLUE = new Color(13, 82, 214);
    private static final Color DEEP_BLUE = new Color(12, 31, 66);
    private static final Color MUTED_TEXT = new Color(110, 119, 138);
    private static final int WINDOW_WIDTH = 1120;
    private static final int WINDOW_HEIGHT = 720;
    private static final int BASE_HERO_WIDTH = 594;
    private static final int BASE_HERO_HEIGHT = 795;
    private static final int HERO_WIDTH = 455;
    private static final int HERO_HEIGHT = 610;
    private static final int FORM_WIDTH = 330;
    private static final int FIELD_HEIGHT = 46;
    private static final int CARD_WIDTH = HERO_WIDTH + 52 + FORM_WIDTH + 56;
    private static final int CARD_HEIGHT = HERO_HEIGHT;
    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
        SignIn.addActionListener(this::SignInActionPerformed);
        
        setHighQualityImage();
        arrangeLoginLikeReference();
        setupPlaceholderLogic();
        this.setLocationRelativeTo(null);
    }

    private void setupPlaceholderLogic() {
        TextPlaceholderUtil.applyPlaceholder(Email, "Enter your email or phone number");
        TextPlaceholderUtil.applyPlaceholder(Password, "Enter your password");
    }

   private void setHighQualityImage() {
    try {
        // 1. Load the image directly from the package
        java.net.URL imgURL = getClass().getResource("/furrealvetclinicmanagement/furry.png"); 
        if (imgURL == null) {
            System.err.println("Could not find image file!");
            return;
        }
        pic.setIcon(createPortraitHeroIcon(imgURL, HERO_WIDTH, HERO_HEIGHT));
        pic.revalidate();
        pic.repaint();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    private void arrangeLoginLikeReference() {
        watermarkPanel1.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        roundedPanel1.setBackground(Color.WHITE);
        roundedPanel1.setCornerRadius(28);

        pic.setPreferredSize(new Dimension(HERO_WIDTH, HERO_HEIGHT));
        pic.setMinimumSize(new Dimension(HERO_WIDTH, HERO_HEIGHT));
        pic.setMaximumSize(new Dimension(HERO_WIDTH, HERO_HEIGHT));
        pic.setRadius(28);

        configureField(Email);
        configureField(Password);
        configureButton(SignIn, BRAND_BLUE, Color.WHITE);
        configureButton(register, Color.WHITE, BRAND_BLUE);

        javax.swing.JLabel brandName = new javax.swing.JLabel("FurReal");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brandName.setForeground(BRAND_BLUE);

        javax.swing.JLabel brandSubtitle = new javax.swing.JLabel("Vet Clinic Management");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brandSubtitle.setForeground(BRAND_BLUE);

        javax.swing.JPanel brandCopy = new javax.swing.JPanel();
        brandCopy.setOpaque(false);
        brandCopy.setLayout(new javax.swing.BoxLayout(brandCopy, javax.swing.BoxLayout.Y_AXIS));
        brandName.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandCopy.add(brandName);
        brandCopy.add(brandSubtitle);

        javax.swing.JPanel brandRow = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);
        brandRow.add(new javax.swing.JLabel(new PawHeartIcon(44)));
        brandRow.add(brandCopy);

        javax.swing.JLabel welcome = new javax.swing.JLabel("Welcome Back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(DEEP_BLUE);

        javax.swing.JLabel subtitle = new javax.swing.JLabel("Sign in to continue to FurReal Vet Clinic Management.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED_TEXT);

        javax.swing.JLabel emailLabel = createFieldLabel("Email or Phone");
        javax.swing.JLabel passwordLabel = createFieldLabel("Password");

        javax.swing.JSeparator leftLine = new javax.swing.JSeparator();
        javax.swing.JSeparator rightLine = new javax.swing.JSeparator();
        javax.swing.JLabel orLabel = new javax.swing.JLabel("or");
        orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orLabel.setForeground(MUTED_TEXT);

        javax.swing.JPanel accountRow = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 0));
        accountRow.setOpaque(false);
        javax.swing.JLabel accountPrompt = new javax.swing.JLabel("Don't have an account?");
        accountPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountPrompt.setForeground(MUTED_TEXT);
        javax.swing.JLabel createOne = new javax.swing.JLabel("Create one");
        createOne.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createOne.setForeground(BRAND_BLUE);
        createOne.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createOne.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                register.doClick();
            }
        });
        accountRow.add(accountPrompt);
        accountRow.add(createOne);

        javax.swing.GroupLayout cardLayout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(cardLayout);
        cardLayout.setHorizontalGroup(
            cardLayout.createSequentialGroup()
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, HERO_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(brandRow, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(welcome, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subtitle, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Email, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SignIn, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cardLayout.createSequentialGroup()
                        .addComponent(leftLine, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(orLabel)
                        .addGap(12, 12, 12)
                        .addComponent(rightLine, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(register, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accountRow, javax.swing.GroupLayout.PREFERRED_SIZE, FORM_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
        );
        cardLayout.setVerticalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, HERO_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(cardLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(brandRow, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(26, 26, 26)
                    .addComponent(welcome, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(4, 4, 4)
                    .addComponent(subtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(22, 22, 22)
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(7, 7, 7)
                    .addComponent(Email, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(passwordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(7, 7, 7)
                    .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(30, 30, 30)
                    .addComponent(SignIn, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(14, 14, 14)
                    .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(leftLine, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(orLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rightLine, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(16, 16, 16)
                    .addComponent(register, javax.swing.GroupLayout.PREFERRED_SIZE, FIELD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(accountRow, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(watermarkPanel1);
        watermarkPanel1.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, CARD_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, CARD_HEIGHT, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        pack();
    }

    private ImageIcon createPortraitHeroIcon(java.net.URL imgURL, int targetWidth, int targetHeight) {
        try {
            int canvasWidth = BASE_HERO_WIDTH;
            int canvasHeight = BASE_HERO_HEIGHT;
            BufferedImage source = ImageIO.read(imgURL);
            BufferedImage output = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = output.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2.setPaint(new GradientPaint(0, 0, new Color(60, 128, 230), canvasWidth, canvasHeight, new Color(174, 213, 255)));
            g2.fillRect(0, 0, canvasWidth, canvasHeight);

            g2.setComposite(AlphaComposite.SrcOver.derive(0.65f));
            drawDotGrid(g2, 34, 34, 7, 6);
            drawDotGrid(g2, 34, canvasHeight - 150, 7, 7);
            drawDotGrid(g2, canvasWidth - 136, 34, 7, 6);
            g2.setComposite(AlphaComposite.SrcOver);

            drawSparkle(g2, 74, 350, 13);
            drawSparkle(g2, canvasWidth - 92, 200, 13);
            drawSparkle(g2, canvasWidth - 66, 328, 10);

            drawIllustrationLineArt(g2, source, 810, 230, 760, 650, 130, 405, 440, 385);

            int centerX = canvasWidth / 2;
            drawHeroLogo(g2, 190, 78);
            drawCenteredShadowText(g2, "Mastering", new Font("Segoe UI", Font.BOLD, 64), centerX, 208);
            drawCenteredShadowText(g2, "the Care", new Font("Segoe UI", Font.BOLD, 64), centerX, 286);

            g2.setColor(new Color(238, 247, 255, 180));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(centerX - 150, 318, centerX - 42, 318);
            g2.drawLine(centerX + 42, 318, centerX + 150, 318);
            drawSmallHeart(g2, centerX, 308);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 19));
            g2.setColor(Color.WHITE);
            String line1 = "We're dedicated to providing";
            String line2 = "excellent care for your fur babies.";
            drawCenteredString(g2, line1, canvasWidth / 2, 365);
            drawCenteredString(g2, line2, canvasWidth / 2, 393);
            g2.dispose();

            if (targetWidth != canvasWidth || targetHeight != canvasHeight) {
                BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D scaledGraphics = scaled.createGraphics();
                scaledGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                scaledGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                scaledGraphics.drawImage(output, 0, 0, targetWidth, targetHeight, null);
                scaledGraphics.dispose();
                return new ImageIcon(scaled);
            }
            return new ImageIcon(output);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void drawHeroLogo(Graphics2D g2, int x, int y) {
        PawHeartIcon.paintMark(g2, x, y, 42, Color.WHITE, new Color(72, 136, 230));
        drawShadowText(g2, "FurReal", new Font("Segoe UI", Font.BOLD, 28), x + 50, y + 30);
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
                    int alpha = Math.min(230, Math.max(90, (155 - brightness) * 3));
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
        g2.drawString(text, x + 3, y + 4);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
    }

    private void drawCenteredShadowText(Graphics2D g2, String text, Font font, int centerX, int y) {
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        drawShadowText(g2, text, font, centerX - metrics.stringWidth(text) / 2, y);
    }

    private void drawCenteredString(Graphics2D g2, String text, int centerX, int baselineY) {
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, centerX - metrics.stringWidth(text) / 2, baselineY);
    }

    private void drawDotGrid(Graphics2D g2, int x, int y, int columns, int rows) {
        g2.setColor(new Color(234, 244, 255));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                g2.fillOval(x + col * 12, y + row * 12, 3, 3);
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
        heart.moveTo(x, y + 17);
        heart.curveTo(x - 17, y + 5, x - 8, y - 6, x, y + 3);
        heart.curveTo(x + 8, y - 6, x + 17, y + 5, x, y + 17);
        heart.closePath();
        g2.fill(heart);
    }

    private javax.swing.JLabel createFieldLabel(String text) {
        javax.swing.JLabel label = new javax.swing.JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(31, 41, 55));
        return label;
    }

    private void configureField(studentenrollmentsystem.RoundedTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(new Color(31, 41, 55));
        field.setCaretColor(BRAND_BLUE);
        field.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 16, 5, 16));
        field.setPreferredSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        field.setMinimumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        field.setMaximumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
    }

    private void configureButton(studentenrollmentsystem.RoundedButton button, Color background, Color foreground) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setPreferredSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        button.setMinimumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
        button.setMaximumSize(new Dimension(FORM_WIDTH, FIELD_HEIGHT));
    }

    private static class PawHeartIcon implements Icon {
        private final int size;

        PawHeartIcon(int size) {
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
            paintMark(g2, x, y, size, BRAND_BLUE, Color.WHITE);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of      this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        watermarkPanel1 = new design.WatermarkPanel();
        roundedPanel1 = new design.RoundedPanel();
        pic = new design.RoundedLabel();
        Password = new studentenrollmentsystem.RoundedTextField();
        Email = new studentenrollmentsystem.RoundedTextField();
        SignIn = new studentenrollmentsystem.RoundedButton();
        register = new studentenrollmentsystem.RoundedButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        watermarkPanel1.setImagePath("/furrealvetclinicmanagement/bgl.png");

        pic.setMaximumSize(new java.awt.Dimension(725, 440));
        pic.setMinimumSize(new java.awt.Dimension(725, 440));
        pic.setPreferredSize(new java.awt.Dimension(725, 440));

        Password.setText("Password");
        Password.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        Email.setText("Email or Phone Number");
        Email.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        SignIn.setText("Sign In");
        SignIn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        register.setText("Register");
        register.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        register.addActionListener(this::registerActionPerformed);

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(362, Short.MAX_VALUE))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 384, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout watermarkPanel1Layout = new javax.swing.GroupLayout(watermarkPanel1);
        watermarkPanel1.setLayout(watermarkPanel1Layout);
        watermarkPanel1Layout.setHorizontalGroup(
            watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(watermarkPanel1Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );
        watermarkPanel1Layout.setVerticalGroup(
            watermarkPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(watermarkPanel1Layout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(watermarkPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(watermarkPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
        java.awt.EventQueue.invokeLater(() -> new Registration().setVisible(true));
        this.dispose();
    }//GEN-LAST:event_registerActionPerformed

    private void SignInActionPerformed(java.awt.event.ActionEvent evt) {
        String identifier = TextPlaceholderUtil.clean(Email);
        String password = TextPlaceholderUtil.clean(Password);

        if (ValidationUtil.hasBlank(identifier) && ValidationUtil.hasBlank(password)) {
            JOptionPane.showMessageDialog(this, "Please enter your email/phone and password.");
            return;
        }

        if (ValidationUtil.hasBlank(identifier)) {
            JOptionPane.showMessageDialog(this, "Please enter your email/phone.");
            return;
        }

        if (ValidationUtil.hasBlank(password)) {
            JOptionPane.showMessageDialog(this, "Please enter a password.");
            return;
        }

        if (!clientDAO.authenticate(identifier, password)) {
            JOptionPane.showMessageDialog(this, "Invalid login credentials.");
            return;
        }

        new Dashboard().setVisible(true);
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
                new Login().setVisible(true);
            }
        });

        /* Create and display the form */
     //   java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private studentenrollmentsystem.RoundedTextField Email;
    private studentenrollmentsystem.RoundedTextField Password;
    private studentenrollmentsystem.RoundedButton SignIn;
    private design.RoundedLabel pic;
    private studentenrollmentsystem.RoundedButton register;
    private design.RoundedPanel roundedPanel1;
    private design.WatermarkPanel watermarkPanel1;
    // End of variables declaration//GEN-END:variables
}
