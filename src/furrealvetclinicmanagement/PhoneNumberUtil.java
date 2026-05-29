package furrealvetclinicmanagement;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class PhoneNumberUtil {
    private PhoneNumberUtil() {
    }

    public static void applyPhoneMask(JTextField field) {
        if (field.getDocument() instanceof AbstractDocument document) {
            document.setDocumentFilter(new PhoneMaskFilter(field));
        }
    }

    public static String format(String value) {
        String digits = digitsOnly(value);
        if (digits.length() <= 4) {
            return digits;
        }
        if (digits.length() <= 7) {
            return digits.substring(0, 4) + "-" + digits.substring(4);
        }
        return digits.substring(0, 4) + "-" + digits.substring(4, 7) + "-" + digits.substring(7);
    }

    public static String digitsOnly(String value) {
        String digits = value == null ? "" : value.replaceAll("\\D", "");
        return digits.length() > 11 ? digits.substring(0, 11) : digits;
    }

    public static boolean isValidPhilippineMobile(String value) {
        String digits = digitsOnly(value);
        return digits.length() == 11 && digits.startsWith("09");
    }

    private static String placeholderText(JTextField field) {
        Object placeholder = field.getClientProperty("placeholder.text");
        return placeholder == null ? "" : placeholder.toString();
    }

    private static class PhoneMaskFilter extends DocumentFilter {
        private final JTextField field;

        private PhoneMaskFilter(JTextField field) {
            this.field = field;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text,
                AttributeSet attrs) throws BadLocationException {
            replace(fb, offset, 0, text, attrs);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                AttributeSet attrs) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + (text == null ? "" : text)
                    + current.substring(offset + length);

            if (next.isBlank() || placeholderText(field).equals(next)) {
                fb.replace(0, fb.getDocument().getLength(), next, attrs);
                return;
            }

            fb.replace(0, fb.getDocument().getLength(), format(next), attrs);
            SwingUtilities.invokeLater(() -> field.setCaretPosition(field.getText().length()));
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            replace(fb, offset, length, "", null);
        }
    }
}
