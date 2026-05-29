package furrealvetclinicmanagement;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class DateInputUtil {
    public static final String DISPLAY_PATTERN = "MM/dd/yyyy";
    private static final Color PLACEHOLDER_COLOR = new Color(148, 163, 184);
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.US)
                    .withResolverStyle(ResolverStyle.STRICT);

    private DateInputUtil() {
    }

    public static void applyDateMask(JTextField field) {
        if (field.getDocument() instanceof AbstractDocument document) {
            document.setDocumentFilter(new DateMaskFilter(field));
        }
        field.putClientProperty("dateInput.foreground", field.getForeground());
        field.setToolTipText(null);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderText(field.getText())) {
                    field.setForeground(originalForeground(field));
                    field.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                showPlaceholderIfNeeded(field);
            }
        });
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshPlaceholderLater(field);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshPlaceholderLater(field);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshPlaceholderLater(field);
            }
        });
        showPlaceholderIfNeeded(field);
    }

    public static String normalizeDate(String value) {
        String cleaned = cleanInput(value);
        if (cleaned.isEmpty()) {
            return "";
        }

        String displayDate = toDisplayDate(cleaned);
        try {
            LocalDate parsedDate = LocalDate.parse(displayDate, DISPLAY_FORMATTER);
            return parsedDate.format(DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static boolean isValidDate(String value) {
        return !normalizeDate(value).isEmpty();
    }

    public static String toDisplayDate(String value) {
        String cleaned = cleanInput(value);
        if (cleaned.isEmpty()) {
            return "";
        }

        try {
            return LocalDate.parse(cleaned, DISPLAY_FORMATTER).format(DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            // Try ISO dates from older database values, such as 2025-12-25.
        }

        try {
            return LocalDate.parse(cleaned, DateTimeFormatter.ISO_LOCAL_DATE).format(DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            return cleaned;
        }
    }

    public static String cleanInput(String value) {
        String cleaned = value == null ? "" : value.trim();
        return isPlaceholderText(cleaned) ? "" : cleaned;
    }

    private static void refreshPlaceholderLater(JTextField field) {
        SwingUtilities.invokeLater(() -> showPlaceholderIfNeeded(field));
    }

    private static void showPlaceholderIfNeeded(JTextField field) {
        if (!field.hasFocus() && field.getText().trim().isEmpty()) {
            field.setForeground(PLACEHOLDER_COLOR);
            field.setText(DISPLAY_PATTERN);
        } else if (!isPlaceholderText(field.getText())) {
            field.setForeground(originalForeground(field));
        }
    }

    private static boolean isPlaceholderText(String value) {
        return DISPLAY_PATTERN.equals(value == null ? "" : value.trim());
    }

    private static Color originalForeground(JTextField field) {
        Object foreground = field.getClientProperty("dateInput.foreground");
        return foreground instanceof Color color ? color : Color.WHITE;
    }

    private static String maskDate(String value) {
        if (isPlaceholderText(value)) {
            return DISPLAY_PATTERN;
        }

        String digits = value == null ? "" : value.replaceAll("\\D", "");
        if (digits.length() > 8) {
            digits = digits.substring(0, 8);
        }

        if (digits.length() <= 2) {
            return digits;
        }
        if (digits.length() <= 4) {
            return digits.substring(0, 2) + "/" + digits.substring(2);
        }
        return digits.substring(0, 2) + "/" + digits.substring(2, 4) + "/" + digits.substring(4);
    }

    private static class DateMaskFilter extends DocumentFilter {
        private final JTextField field;

        private DateMaskFilter(JTextField field) {
            this.field = field;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                AttributeSet attrs) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + (text == null ? "" : text)
                    + current.substring(offset + length);
            fb.replace(0, fb.getDocument().getLength(), maskDate(next), attrs);
            SwingUtilities.invokeLater(() -> field.setCaretPosition(field.getText().length()));
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            replace(fb, offset, length, "", null);
        }
    }
}
