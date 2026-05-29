package furrealvetclinicmanagement;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public final class TextFieldFocusUtil {
    private TextFieldFocusUtil() {
    }

    public static void install(Container root) {
        if (root == null) {
            return;
        }

        root.setFocusable(true);
        installFocusOutOnClick(root, root);
    }

    private static void installFocusOutOnClick(Component component, Component focusTarget) {
        if (!(component instanceof JTextField)) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    Component focusOwner = KeyboardFocusManager
                            .getCurrentKeyboardFocusManager().getFocusOwner();
                    Component clicked = evt.getComponent();
                    if (focusOwner instanceof JTextField
                            && !SwingUtilities.isDescendingFrom(clicked, focusOwner)) {
                        Component target = clicked.isFocusable() ? clicked : focusTarget;
                        target.requestFocusInWindow();
                    }
                }
            });
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                installFocusOutOnClick(child, focusTarget);
            }
        }
    }
}
