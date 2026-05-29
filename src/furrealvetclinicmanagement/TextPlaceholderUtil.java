package furrealvetclinicmanagement;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

public final class TextPlaceholderUtil {
    private static final Color PLACEHOLDER_COLOR = new Color(148, 163, 184);

    private TextPlaceholderUtil() {
    }

    public static void applyPlaceholder(JTextField field, String placeholder) {
        field.putClientProperty("placeholder.text", placeholder);
        field.putClientProperty("placeholder.foreground", field.getForeground());
        showPlaceholder(field);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholder(field)) {
                    field.setText("");
                    field.setForeground(originalForeground(field));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    showPlaceholder(field);
                }
            }
        });
    }

    public static String clean(JTextField field) {
        return isPlaceholder(field) ? "" : field.getText().trim();
    }

    public static void resetPlaceholder(JTextField field) {
        showPlaceholder(field);
    }

    private static void showPlaceholder(JTextField field) {
        field.setForeground(PLACEHOLDER_COLOR);
        field.setText(placeholderText(field));
    }

    private static boolean isPlaceholder(JTextField field) {
        return placeholderText(field).equals(field.getText());
    }

    private static String placeholderText(JTextField field) {
        Object placeholder = field.getClientProperty("placeholder.text");
        return placeholder == null ? "" : placeholder.toString();
    }

    private static Color originalForeground(JTextField field) {
        Object foreground = field.getClientProperty("placeholder.foreground");
        return foreground instanceof Color color ? color : Color.BLACK;
    }
}
