/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package furrealvetclinicmanagement;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Asus
 */
public class Services extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Services.class.getName());
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final List<Integer> serviceIds = new ArrayList<>();
    private int selectedServiceId = -1;
    private boolean loadingServices;
    private boolean revertingTableEdit;
    private AWTEventListener tableSelectionClearer;

    /**
     * Creates new form Services
     */
    public Services() {
        initComponents();
        
        design.DarkTableStyler.apply(jTable1, jScrollPane1);
        
        setHighQualityImage(); 
        setupServiceLogic();
        
        this.setLocationRelativeTo(null);
    }
    
   private void setHighQualityImage() {
    try {
        // 1. Load the image directly from the package
        java.net.URL imgURL = getClass().getResource("/furrealvetclinicmanagement/Setback.png");  
        if (imgURL == null) {
            System.err.println("Could not find image file!");
            return;
        }
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image originalImg = originalIcon.getImage();

        // 2. Use SCALE_SMOOTH to keep text and fine details razor sharp
        Image scaledImg = originalImg.getScaledInstance(54, 54, Image.SCALE_SMOOTH);

        // 3. Apply it to your label
        set.setIcon(new ImageIcon(scaledImg)); 
        set.revalidate();
        set.repaint();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void setupServiceLogic() {
        DateInputUtil.applyDateMask(Search);
        DateInputUtil.applyDateMask(Date);
        setupServiceTableEditing();
        installSelectionClearer(getContentPane());
        installWindowSelectionClearer();
        Add.addActionListener(evt -> addService());
        Update.addActionListener(evt -> updateService());
        Delete.addActionListener(evt -> deleteService());
        roundedButton1.addActionListener(evt -> refreshAll());
        set.addActionListener(evt -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        Search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadServices();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadServices();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadServices();
            }
        });

        for (AbstractButton category : categoryButtons()) {
            category.addActionListener(evt -> {
                if (category.isSelected()) {
                    clearOtherCategories(category);
                }
                loadServices();
            });
        }

        jTable1.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                selectServiceFromTable();
            }
        });

        loadServices();
    }

    private void installWindowSelectionClearer() {
        tableSelectionClearer = event -> {
            if (!(event instanceof MouseEvent mouseEvent)
                    || mouseEvent.getID() != MouseEvent.MOUSE_PRESSED
                    || !(mouseEvent.getSource() instanceof Component source)) {
                return;
            }

            if (!SwingUtilities.isDescendingFrom(source, this)
                    || source == jTable1
                    || SwingUtilities.isDescendingFrom(source, jTable1)) {
                return;
            }

            clearServiceTableHighlight();
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(tableSelectionClearer, AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    public void dispose() {
        if (tableSelectionClearer != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(tableSelectionClearer);
            tableSelectionClearer = null;
        }
        super.dispose();
    }

    private void installSelectionClearer(Component component) {
        if (component == jTable1) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    if (jTable1.rowAtPoint(evt.getPoint()) < 0) {
                        clearServiceTableHighlight();
                    }
                }
            });
            return;
        }

        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                clearServiceTableHighlight();
            }
        });

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                installSelectionClearer(child);
            }
        }
    }

    private void clearServiceTableHighlight() {
        if (jTable1.isEditing()) {
            TableCellEditor editor = jTable1.getCellEditor();
            boolean editSaved = editor == null || editor.stopCellEditing();
            if (!editSaved && jTable1.isEditing()) {
                editor.cancelCellEditing();
            }
            if (jTable1.isEditing()) {
                jTable1.removeEditor();
            }
        }
        jTable1.clearSelection();
        jTable1.repaint();
    }

    private void setupServiceTableEditing() {
        jTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        JComboBox<String> categoryEditor = new JComboBox<>(
                new String[] {"General", "Vaccines", "Dental", "Grooming"});
        jTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(categoryEditor));

        jTable1.getModel().addTableModelListener(evt -> {
            if (loadingServices || revertingTableEdit
                    || evt.getType() != TableModelEvent.UPDATE || evt.getFirstRow() < 0) {
                return;
            }
            saveTableEdit(evt.getFirstRow());
        });
    }

    private void loadServices() {
        selectedServiceId = -1;
        loadingServices = true;
        try {
            serviceDAO.loadServicesToTable((DefaultTableModel) jTable1.getModel(),
                    serviceIds, DateInputUtil.cleanInput(Search.getText()), selectedCategory());
            jTable1.clearSelection();
            clearServiceFields();
        } finally {
            loadingServices = false;
        }
    }

    private void addService() {
        ServiceFormData formData = readFormData();
        if (formData == null) {
            return;
        }

        if (serviceDAO.addService(formData.serviceName, formData.category,
                formData.basePrice, formData.serviceDate)) {
            loadServices();
        } else {
            JOptionPane.showMessageDialog(this, "Service was not added. Please check the database connection.");
        }
    }

    private void updateService() {
        if (selectedServiceId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service first.");
            return;
        }

        ServiceFormData formData = readFormData();
        if (formData == null) {
            return;
        }

        if (serviceDAO.updateService(selectedServiceId, formData.serviceName,
                formData.category, formData.basePrice, formData.serviceDate)) {
            loadServices();
        } else {
            JOptionPane.showMessageDialog(this, "Service was not updated. Please check the database connection.");
        }
    }

    private void deleteService() {
        if (selectedServiceId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected service?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (serviceDAO.deleteService(selectedServiceId)) {
            loadServices();
        } else {
            JOptionPane.showMessageDialog(this, "Service was not deleted. Please check the database connection.");
        }
    }

    private void selectServiceFromTable() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }

        int modelRow = jTable1.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= serviceIds.size()) {
            selectedServiceId = -1;
            clearServiceFields();
            return;
        }

        selectedServiceId = serviceIds.get(modelRow);
        ServiceName.setText(jTable1.getModel().getValueAt(modelRow, 0).toString());
        setCategoryCombo(jTable1.getModel().getValueAt(modelRow, 1).toString());
        Price.setText(cleanPriceText(jTable1.getModel().getValueAt(modelRow, 2).toString()));
        Date.setText(DateInputUtil.toDisplayDate(jTable1.getModel().getValueAt(modelRow, 3).toString()));
    }

    private void saveTableEdit(int modelRow) {
        if (modelRow >= serviceIds.size()) {
            return;
        }

        int serviceId = serviceIds.get(modelRow);
        String serviceName = tableValue(modelRow, 0);
        String category = canonicalCategory(tableValue(modelRow, 1));
        String priceText = cleanPriceText(tableValue(modelRow, 2));
        String serviceDate = DateInputUtil.normalizeDate(DateInputUtil.cleanInput(tableValue(modelRow, 3)));

        if (ValidationUtil.hasBlank(serviceName, category, priceText, serviceDate)) {
            rejectTableEdit("Please complete service name, category, base price, and date.");
            return;
        }

        Double basePrice = parseBasePrice(priceText);
        if (basePrice == null) {
            rejectTableEdit("Please enter a valid base price.");
            return;
        }

        if (!serviceDAO.updateService(serviceId, serviceName, category, basePrice, serviceDate)) {
            JOptionPane.showMessageDialog(this, "Service was not updated. Please check the database connection.");
        }
        reloadServicesLater();
    }

    private void rejectTableEdit(String message) {
        revertingTableEdit = true;
        SwingUtilities.invokeLater(() -> {
            try {
                JOptionPane.showMessageDialog(this, message);
                loadServices();
                jTable1.clearSelection();
                jTable1.repaint();
            } finally {
                revertingTableEdit = false;
            }
        });
    }

    private String tableValue(int row, int column) {
        Object value = jTable1.getModel().getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
    }

    private void reloadServicesLater() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                loadServices();
                jTable1.clearSelection();
                jTable1.repaint();
            });
        });
    }

    private ServiceFormData readFormData() {
        String serviceName = ValidationUtil.clean(ServiceName.getText(), "Service Name");
        String category = categoryFromCombo();
        String priceText = ValidationUtil.clean(Price.getText(), "Base Price");
        String serviceDate = DateInputUtil.cleanInput(ValidationUtil.clean(Date.getText(), "Date"));

        if (ValidationUtil.hasBlank(serviceName, category, priceText, serviceDate)) {
            JOptionPane.showMessageDialog(this, "Please complete service name, category, base price, and date.");
            return null;
        }
        serviceDate = DateInputUtil.normalizeDate(serviceDate);
        if (serviceDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date using MM/dd/yyyy.");
            return null;
        }

        Double basePrice = parseBasePrice(priceText);
        if (basePrice == null) {
            JOptionPane.showMessageDialog(this, "Please enter a valid base price.");
            return null;
        }

        return new ServiceFormData(serviceName, category, basePrice, serviceDate);
    }

    private Double parseBasePrice(String priceText) {
        try {
            return Double.parseDouble(cleanPriceText(priceText));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String cleanPriceText(String priceText) {
        return priceText.replace("Php", "").replace(",", "").trim();
    }

    private List<AbstractButton> categoryButtons() {
        return List.of(roundCheckBox1, roundCheckBox2, roundCheckBox3, roundCheckBox5);
    }

    private void clearOtherCategories(AbstractButton selectedCategory) {
        for (AbstractButton category : categoryButtons()) {
            if (category != selectedCategory) {
                category.setSelected(false);
            }
        }
    }

    private String selectedCategory() {
        for (AbstractButton category : categoryButtons()) {
            if (category.isSelected()) {
                return canonicalCategory(category.getText());
            }
        }
        return "";
    }

    private String categoryFromCombo() {
        if (jComboBox1.getSelectedItem() == null) {
            return "";
        }
        return canonicalCategory(jComboBox1.getSelectedItem().toString().trim());
    }

    private String canonicalCategory(String category) {
        if ("Vaccine".equalsIgnoreCase(category) || "Vaccines".equalsIgnoreCase(category)) {
            return "Vaccines";
        }
        return category == null ? "" : category.trim();
    }

    private void setCategoryCombo(String category) {
        String canonical = canonicalCategory(category);
        for (int i = 0; i < jComboBox1.getItemCount(); i++) {
            if (canonicalCategory(jComboBox1.getItemAt(i)).equalsIgnoreCase(canonical)) {
                jComboBox1.setSelectedIndex(i);
                return;
            }
        }
        jComboBox1.setSelectedIndex(0);
    }

    private void refreshAll() {
        Search.setText("");
        for (AbstractButton category : categoryButtons()) {
            category.setSelected(false);
        }
        loadServices();
    }

    private void clearServiceFields() {
        ServiceName.setText("");
        Price.setText("");
        Date.setText("");
        jComboBox1.setSelectedIndex(0);
    }

    private static class ServiceFormData {
        private final String serviceName;
        private final String category;
        private final double basePrice;
        private final String serviceDate;

        private ServiceFormData(String serviceName, String category,
                double basePrice, String serviceDate) {
            this.serviceName = serviceName;
            this.category = category;
            this.basePrice = basePrice;
            this.serviceDate = serviceDate;
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

        jPanel1 = new javax.swing.JPanel();
        roundedPanel1 = new design.RoundedPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        roundedButton1 = new studentenrollmentsystem.RoundedButton();
        roundedPanel2 = new design.RoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Search = new design.RoundTextField();
        roundCheckBox1 = new design.RoundCheckBox();
        roundCheckBox2 = new design.RoundCheckBox();
        roundCheckBox3 = new design.RoundCheckBox();
        roundCheckBox5 = new design.RoundCheckBox();
        roundedPanel3 = new design.RoundedPanel();
        jLabel10 = new javax.swing.JLabel();
        roundedPanel4 = new design.RoundedPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ServiceName = new design.RoundTextField();
        jLabel13 = new javax.swing.JLabel();
        Price = new design.RoundTextField();
        jLabel15 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        Date = new design.RoundTextField();
        Add = new studentenrollmentsystem.RoundedButton();
        Delete = new studentenrollmentsystem.RoundedButton();
        Update = new studentenrollmentsystem.RoundedButton();
        jLabel1 = new javax.swing.JLabel();
        set = new design.SleekIconButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(24, 28, 36));

        roundedPanel1.setBackground(new java.awt.Color(33, 38, 48));

        jTable1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Service Name", "Category", "Base Price", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 153, 255));
        jLabel3.setText("2. Service Catalog");

        roundedButton1.setBackground(new java.awt.Color(33, 38, 48));
        roundedButton1.setForeground(new java.awt.Color(255, 255, 255));
        roundedButton1.setText("Refresh");
        roundedButton1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundedButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        roundedPanel2.setBackground(new java.awt.Color(33, 38, 48));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 153, 255));
        jLabel2.setText("1.  Search & Filter");

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Categories");

        jLabel14.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Search:");

        Search.setForeground(new java.awt.Color(255, 255, 255));

        roundCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        roundCheckBox1.setText("General");
        roundCheckBox1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        roundCheckBox2.setForeground(new java.awt.Color(255, 255, 255));
        roundCheckBox2.setText("Vaccines");
        roundCheckBox2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        roundCheckBox2.addActionListener(this::roundCheckBox2ActionPerformed);

        roundCheckBox3.setForeground(new java.awt.Color(255, 255, 255));
        roundCheckBox3.setText("Dental");
        roundCheckBox3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        roundCheckBox5.setForeground(new java.awt.Color(255, 255, 255));
        roundCheckBox5.setText("Grooming");
        roundCheckBox5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(roundCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(roundCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(roundCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(roundCheckBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roundCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roundCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roundCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roundCheckBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        roundedPanel3.setBackground(new java.awt.Color(33, 38, 48));

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 153, 255));
        jLabel10.setText("3. Administrative Controls");

        roundedPanel4.setBackground(new java.awt.Color(24, 28, 36));

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Service Details");

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Service Name");

        ServiceName.setForeground(new java.awt.Color(255, 255, 255));
        ServiceName.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Base Price");

        Price.setForeground(new java.awt.Color(255, 255, 255));
        Price.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Category");

        jComboBox1.setBackground(new java.awt.Color(26, 31, 41));
        jComboBox1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(255, 255, 255));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "General", "Vaccines", "Dental", "Grooming" }));
        jComboBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Date");

        Date.setForeground(new java.awt.Color(255, 255, 255));
        Date.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel4Layout = new javax.swing.GroupLayout(roundedPanel4);
        roundedPanel4.setLayout(roundedPanel4Layout);
        roundedPanel4Layout.setHorizontalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundedPanel4Layout.createSequentialGroup()
                        .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(roundedPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Price, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ServiceName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44)
                        .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(roundedPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(roundedPanel4Layout.createSequentialGroup()
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 66, Short.MAX_VALUE)))))))
                .addGap(24, 24, 24))
        );
        roundedPanel4Layout.setVerticalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        Add.setBackground(new java.awt.Color(46, 115, 50));
        Add.setForeground(new java.awt.Color(255, 255, 255));
        Add.setText("Add Service");
        Add.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        Delete.setBackground(new java.awt.Color(140, 46, 46));
        Delete.setForeground(new java.awt.Color(255, 255, 255));
        Delete.setText("Delete");
        Delete.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        Update.setBackground(new java.awt.Color(30, 63, 102));
        Update.setForeground(new java.awt.Color(255, 255, 255));
        Update.setText("Update");
        Update.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addComponent(roundedPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Add, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                            .addComponent(Update, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(Add, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Update, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Services Menu Manager");

        set.setText("sleekIconButton1");
        set.setMaximumSize(new java.awt.Dimension(54, 54));
        set.setMinimumSize(new java.awt.Dimension(54, 54));
        set.setPreferredSize(new java.awt.Dimension(54, 54));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(roundedPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(set, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(set, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundedPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void roundCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roundCheckBox2ActionPerformed

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
                new Services().setVisible(true);
            }
        });
        
        /* Create and display the form */
     //   java.awt.EventQueue.invokeLater(() -> new Services().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private studentenrollmentsystem.RoundedButton Add;
    private design.RoundTextField Date;
    private studentenrollmentsystem.RoundedButton Delete;
    private design.RoundTextField Price;
    private design.RoundTextField Search;
    private design.RoundTextField ServiceName;
    private studentenrollmentsystem.RoundedButton Update;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private design.RoundCheckBox roundCheckBox1;
    private design.RoundCheckBox roundCheckBox2;
    private design.RoundCheckBox roundCheckBox3;
    private design.RoundCheckBox roundCheckBox5;
    private studentenrollmentsystem.RoundedButton roundedButton1;
    private design.RoundedPanel roundedPanel1;
    private design.RoundedPanel roundedPanel2;
    private design.RoundedPanel roundedPanel3;
    private design.RoundedPanel roundedPanel4;
    private design.SleekIconButton set;
    // End of variables declaration//GEN-END:variables
}
