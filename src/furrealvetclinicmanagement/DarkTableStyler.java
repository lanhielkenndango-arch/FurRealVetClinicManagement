/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package furrealvetclinicmanagement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Asus
 */
public class DarkTableStyler {

    public static void apply(JTable table, JScrollPane scrollPane) {
        // --- 1. THE COLOR PALETTE ---
        Color bgDark = new Color(33, 38, 48);        // The dark gray row background
        Color headerDark = new Color(24, 28, 36);    // The darker slate header background
        Color textWhite = new Color(230, 230, 230);  // Clean white text
        Color selectionBlue = new Color(25, 90, 170);// The vibrant blue highlight row
        Color gridLineColor = new Color(50, 55, 65); // Subtle faint lines between rows

        // --- 2. STYLE THE TABLE BODY ---
        table.setBackground(bgDark);
        table.setForeground(textWhite);
        table.setSelectionBackground(selectionBlue);
        table.setSelectionForeground(Color.WHITE);

        // Remove ugly vertical lines, keep faint horizontal ones like the picture
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(gridLineColor);

        // Make rows taller for that modern, breathable UI look
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBorder(BorderFactory.createEmptyBorder());

        // --- 3. STYLE THE HEADER ---
        table.getTableHeader().setBackground(headerDark);
        table.getTableHeader().setForeground(textWhite);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(100, 45));

        // Remove the default 3D borders on the header columns
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gridLineColor));

        // --- 4. STYLE THE SCROLL PANE (Very Important!) ---
        // If you don't do this, the empty space below your rows will be bright white
        scrollPane.setBackground(bgDark);
        scrollPane.getViewport().setBackground(bgDark);
        scrollPane.setBorder(BorderFactory.createLineBorder(gridLineColor, 1));

        // --- 5. CENTER ALIGN THE TEXT (Optional but looks great) ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
