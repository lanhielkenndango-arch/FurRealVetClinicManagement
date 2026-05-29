/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package furrealvetclinicmanagement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Asus
 */
public class VisitAndTransaction extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VisitAndTransaction.class.getName());
    private final PetDAO petDAO = new PetDAO();
    private final VisitDAO visitDAO = new VisitDAO();
    private final List<Integer> petIds = new ArrayList<>();
    private final List<Integer> clientIds = new ArrayList<>();
    private final List<Integer> visitRecordIds = new ArrayList<>();
    private final Map<AbstractButton, Double> servicePrices = new LinkedHashMap<>();
    private int selectedPetId = -1;
    private int selectedClientId = -1;
    private int selectedVisitId = -1;
    private JTable visitRecordsTable;
    private JTable visitDetailsTable;
    private design.RoundTextField visitRecordSearch;
    private design.RoundTextField visitRecordDateSearch;
    private design.RoundTextField editVisitDate;
    private JComboBox<String> visitStatusFilter;
    private JComboBox<String> editVisitStatus;

    /**
     * Creates new form VisitAndTransaction
     */
    public VisitAndTransaction() {
        initComponents();
        
        design.DarkTableStyler.apply(jTable1, jScrollPane1);
        setupVisitLogic();
        setupVisitRecordsTab();
        
        this.setLocationRelativeTo(null);
    }

    private void setupVisitLogic() {
        Price.setEditable(false);
        Price.setText("Price: Php 0.00");
        DateInputUtil.applyDateMask(VisitDate);
        jTable1.setDefaultEditor(Object.class, null);
        TextPlaceholderUtil.applyPlaceholder(SearchBar, "Owner ID");

        servicePrices.put(vaccine, 350.00);
        servicePrices.put(teethcleaning, 1500.00);
        servicePrices.put(earcleaning, 250.00);
        servicePrices.put(nailtrim, 150.00);
        servicePrices.put(checkup, 100.00);
        servicePrices.put(haircut, 500.00);
        servicePrices.put(style, 700.00);
        servicePrices.put(fleaandtickdips, 400.00);

        for (AbstractButton service : servicePrices.keySet()) {
            service.addActionListener(evt -> updatePriceText());
        }

        SearchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadPets();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadPets();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadPets();
            }
        });

        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                selectPetFromTable();
            }
        });

        jTable1.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                selectPetFromTable();
            }
        });

        SavingVisit.addActionListener(evt -> saveVisit());
        Back.addActionListener(evt -> openDashboard());
        Refresh.addActionListener(evt -> refreshAll());
        loadPets();
    }

    private void loadPets() {
        selectedPetId = -1;
        selectedClientId = -1;
        String ownerId = ValidationUtil.clean(SearchBar.getText(), "Owner ID");
        petDAO.loadAllPetsToTable((DefaultTableModel) jTable1.getModel(), petIds, clientIds, ownerId);
        jTable1.clearSelection();
    }

    private void selectPetFromTable() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }

        int modelRow = jTable1.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= petIds.size() || modelRow >= clientIds.size()) {
            selectedPetId = -1;
            selectedClientId = -1;
            return;
        }

        selectedPetId = petIds.get(modelRow);
        selectedClientId = clientIds.get(modelRow);
    }

    private void updatePriceText() {
        Price.setText(String.format("Price: Php %.2f", calculateTotal()));
    }

    private double calculateTotal() {
        double total = 0.00;
        for (Map.Entry<AbstractButton, Double> service : servicePrices.entrySet()) {
            if (service.getKey().isSelected()) {
                total += service.getValue();
            }
        }
        return total;
    }

    private Map<String, Double> selectedServices() {
        Map<String, Double> selected = new LinkedHashMap<>();
        for (Map.Entry<AbstractButton, Double> service : servicePrices.entrySet()) {
            if (service.getKey().isSelected()) {
                selected.put(service.getKey().getText(), service.getValue());
            }
        }
        return selected;
    }

    private void refreshAll() {
        TextPlaceholderUtil.resetPlaceholder(SearchBar);
        VisitDate.setText("");
        for (AbstractButton service : servicePrices.keySet()) {
            service.setSelected(false);
        }
        updatePriceText();
        loadPets();
    }

    private void openDashboard() {
        new Dashboard().setVisible(true);
        dispose();
    }

    private void saveVisit() {
        if (selectedPetId == -1 || selectedClientId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a pet first.");
            return;
        }

        Map<String, Double> services = selectedServices();
        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service.");
            return;
        }

        String visitDate = DateInputUtil.cleanInput(ValidationUtil.clean(VisitDate.getText(), "Visit Date"));
        if (visitDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the visitation date.");
            return;
        }
        visitDate = DateInputUtil.normalizeDate(visitDate);
        if (visitDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid visitation date using MM/dd/yyyy.");
            return;
        }

        if (visitDAO.addVisit(selectedClientId, selectedPetId, visitDate, services)) {
            JOptionPane.showMessageDialog(this, "Visit saved successfully.");
            refreshAll();
            loadVisitRecords();
        } else {
            JOptionPane.showMessageDialog(this, "Visit was not saved. Please check the database connection.");
        }
    }

    private void setupVisitRecordsTab() {
        JPanel recordsPanel = new JPanel(new BorderLayout(12, 12));
        recordsPanel.setBackground(new Color(24, 28, 36));
        recordsPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        recordsPanel.setPreferredSize(jPanel1.getPreferredSize());

        JPanel filterPanel = createVisitFilterPanel();
        visitRecordsTable = createVisitRecordsTable();
        JScrollPane recordsScrollPane = new JScrollPane(visitRecordsTable);
        design.DarkTableStyler.apply(visitRecordsTable, recordsScrollPane);

        JPanel bottomPanel = new JPanel(new BorderLayout(12, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(createVisitEditPanel(), BorderLayout.WEST);
        bottomPanel.add(createVisitDetailsPanel(), BorderLayout.CENTER);

        recordsPanel.add(filterPanel, BorderLayout.NORTH);
        recordsPanel.add(recordsScrollPane, BorderLayout.CENTER);
        recordsPanel.add(bottomPanel, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("New Visit", jPanel1);
        tabs.addTab("Visit Records", recordsPanel);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);

        loadVisitRecords();
        pack();
    }

    private JPanel createVisitFilterPanel() {
        JPanel filterPanel = darkPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGridBagConstraints();

        JLabel title = sectionLabel("Saved Visits");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        filterPanel.add(title, gbc);

        visitRecordSearch = new design.RoundTextField();
        visitRecordDateSearch = new design.RoundTextField();
        visitStatusFilter = new JComboBox<>(new String[] {"All", "Scheduled", "Completed", "Cancelled"});
        TextPlaceholderUtil.applyPlaceholder(visitRecordSearch, "Owner, pet, or visit ID");
        DateInputUtil.applyDateMask(visitRecordDateSearch);

        addVisitSearchListener(visitRecordSearch);
        addVisitSearchListener(visitRecordDateSearch);
        visitStatusFilter.addActionListener(evt -> loadVisitRecords());

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        filterPanel.add(fieldLabel("Search:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        filterPanel.add(visitRecordSearch, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        filterPanel.add(fieldLabel("Date:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        filterPanel.add(visitRecordDateSearch, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        filterPanel.add(fieldLabel("Status:"), gbc);
        gbc.gridx = 5;
        gbc.weightx = 0.3;
        filterPanel.add(visitStatusFilter, gbc);

        studentenrollmentsystem.RoundedButton refresh = recordButton("Refresh", new Color(33, 38, 48));
        refresh.addActionListener(evt -> {
            TextPlaceholderUtil.resetPlaceholder(visitRecordSearch);
            visitRecordDateSearch.setText("");
            visitStatusFilter.setSelectedIndex(0);
            loadVisitRecords();
        });

        gbc.gridx = 6;
        gbc.weightx = 0;
        filterPanel.add(refresh, gbc);
        return filterPanel;
    }

    private JTable createVisitRecordsTable() {
        JTable table = new JTable(new DefaultTableModel(
                new Object[][] {},
                new String[] {"Visit ID", "Owner", "Pet", "Date", "Status", "Total"}) {
            private final Class<?>[] types = {
                Integer.class, String.class, String.class, String.class, String.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);
        table.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                selectVisitRecord();
            }
        });
        return table;
    }

    private JPanel createVisitEditPanel() {
        JPanel editPanel = darkPanel(new GridBagLayout());
        editPanel.setPreferredSize(new Dimension(300, 170));
        GridBagConstraints gbc = baseGridBagConstraints();

        JLabel title = sectionLabel("Update Visit");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        editPanel.add(title, gbc);

        editVisitDate = new design.RoundTextField();
        editVisitStatus = new JComboBox<>(new String[] {"Scheduled", "Completed", "Cancelled"});
        DateInputUtil.applyDateMask(editVisitDate);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        editPanel.add(fieldLabel("Date:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        editPanel.add(editVisitDate, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        editPanel.add(fieldLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        editPanel.add(editVisitStatus, gbc);

        studentenrollmentsystem.RoundedButton update = recordButton("Update", new Color(13, 82, 214));
        update.addActionListener(evt -> updateSelectedVisit());
        studentenrollmentsystem.RoundedButton delete = recordButton("Delete", new Color(180, 45, 45));
        delete.addActionListener(evt -> deleteSelectedVisit());

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        editPanel.add(update, gbc);
        gbc.gridx = 1;
        editPanel.add(delete, gbc);
        return editPanel;
    }

    private JPanel createVisitDetailsPanel() {
        JPanel detailsPanel = darkPanel(new BorderLayout(8, 8));
        JLabel title = sectionLabel("Visit Service Details");
        detailsPanel.add(title, BorderLayout.NORTH);

        visitDetailsTable = new JTable(new DefaultTableModel(
                new Object[][] {},
                new String[] {"Service", "Category", "Qty", "Line Total"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        visitDetailsTable.setDefaultEditor(Object.class, null);
        JScrollPane detailsScrollPane = new JScrollPane(visitDetailsTable);
        detailsScrollPane.setPreferredSize(new Dimension(520, 138));
        design.DarkTableStyler.apply(visitDetailsTable, detailsScrollPane);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        return detailsPanel;
    }

    private void addVisitSearchListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadVisitRecords();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadVisitRecords();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadVisitRecords();
            }
        });
    }

    private void loadVisitRecords() {
        if (visitRecordsTable == null) {
            return;
        }

        String keyword = TextPlaceholderUtil.clean(visitRecordSearch);
        String date = DateInputUtil.cleanInput(visitRecordDateSearch.getText());
        String status = visitStatusFilter.getSelectedIndex() <= 0
                ? ""
                : visitStatusFilter.getSelectedItem().toString();
        visitDAO.loadVisitsToTable((DefaultTableModel) visitRecordsTable.getModel(),
                visitRecordIds, keyword, date, status);
        clearSelectedVisitRecord();
    }

    private void selectVisitRecord() {
        int row = visitRecordsTable.getSelectedRow();
        if (row < 0) {
            clearSelectedVisitRecord();
            return;
        }

        int modelRow = visitRecordsTable.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= visitRecordIds.size()) {
            clearSelectedVisitRecord();
            return;
        }

        selectedVisitId = visitRecordIds.get(modelRow);
        editVisitDate.setText(visitRecordsTable.getModel().getValueAt(modelRow, 3).toString());
        editVisitStatus.setSelectedItem(visitRecordsTable.getModel().getValueAt(modelRow, 4).toString());
        visitDAO.loadVisitServicesToTable((DefaultTableModel) visitDetailsTable.getModel(), selectedVisitId);
    }

    private void clearSelectedVisitRecord() {
        selectedVisitId = -1;
        if (visitRecordsTable != null) {
            visitRecordsTable.clearSelection();
        }
        if (editVisitDate != null) {
            editVisitDate.setText("");
        }
        if (editVisitStatus != null) {
            editVisitStatus.setSelectedIndex(0);
        }
        if (visitDetailsTable != null) {
            ((DefaultTableModel) visitDetailsTable.getModel()).setRowCount(0);
        }
    }

    private void updateSelectedVisit() {
        if (selectedVisitId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a saved visit first.");
            return;
        }

        String date = DateInputUtil.normalizeDate(editVisitDate.getText());
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid visit date using MM/dd/yyyy.");
            return;
        }

        String status = editVisitStatus.getSelectedItem() == null ? "" : editVisitStatus.getSelectedItem().toString();
        if (ValidationUtil.hasBlank(status)) {
            JOptionPane.showMessageDialog(this, "Please select a visit status.");
            return;
        }

        if (visitDAO.updateVisit(selectedVisitId, date, status)) {
            JOptionPane.showMessageDialog(this, "Visit updated successfully.");
            loadVisitRecords();
        } else {
            JOptionPane.showMessageDialog(this, "Visit was not updated. Please check the database connection.");
        }
    }

    private void deleteSelectedVisit() {
        if (selectedVisitId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a saved visit first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete visit #" + selectedVisitId + "? This also removes its service details.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (visitDAO.deleteVisit(selectedVisitId)) {
            JOptionPane.showMessageDialog(this, "Visit deleted successfully.");
            loadVisitRecords();
        } else {
            JOptionPane.showMessageDialog(this, "Visit was not deleted. Please check the database connection.");
        }
    }

    private JPanel darkPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(33, 38, 48));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));
        return label;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        return label;
    }

    private GridBagConstraints baseGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private studentenrollmentsystem.RoundedButton recordButton(String text, Color background) {
        studentenrollmentsystem.RoundedButton button = new studentenrollmentsystem.RoundedButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        return button;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SearchBar = new design.RoundTextField();
        jLabel7 = new javax.swing.JLabel();
        Back = new studentenrollmentsystem.RoundedButton();
        Refresh = new studentenrollmentsystem.RoundedButton();
        roundedPanel2 = new design.RoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        vaccine = new design.RoundCheckBox();
        teethcleaning = new design.RoundCheckBox();
        earcleaning = new design.RoundCheckBox();
        nailtrim = new design.RoundCheckBox();
        checkup = new design.RoundCheckBox();
        haircut = new design.RoundCheckBox();
        style = new design.RoundCheckBox();
        fleaandtickdips = new design.RoundCheckBox();
        roundedPanel3 = new design.RoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        VisitDate = new design.RoundTextField();
        Price = new design.RoundTextField();
        jLabel8 = new javax.swing.JLabel();
        SavingVisit = new studentenrollmentsystem.RoundedButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(24, 28, 36));

        roundedPanel1.setBackground(new java.awt.Color(33, 38, 48));

        jTable1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pet Name", "Type", "Breed", "Age"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("1. A formto select pet.");

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Select or Search Pet");

        SearchBar.addActionListener(this::SearchBarActionPerformed);

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Search:");

        Back.setBackground(new java.awt.Color(33, 38, 48));
        Back.setForeground(new java.awt.Color(255, 255, 255));
        Back.setText("Back ");
        Back.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        Refresh.setBackground(new java.awt.Color(33, 38, 48));
        Refresh.setForeground(new java.awt.Color(255, 255, 255));
        Refresh.setText("Refresh");
        Refresh.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(roundedPanel1Layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(roundedPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(roundedPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundedPanel1Layout.createSequentialGroup()
                                .addComponent(Back, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Refresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Back, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        roundedPanel2.setBackground(new java.awt.Color(33, 38, 48));

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("2. List of Services");

        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Click the service you want .");

        vaccine.setText("Rabbies Vaccine");
        vaccine.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        teethcleaning.setText("Teeth Cleaning");
        teethcleaning.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        earcleaning.setText("Ear Cleaning");
        earcleaning.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        nailtrim.setText("Nail Trimming");
        nailtrim.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        checkup.setText("Checkup");
        checkup.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        haircut.setText("Haircutting");
        haircut.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        style.setText("Styling");
        style.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        fleaandtickdips.setText("Flea and Tick Dips");
        fleaandtickdips.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(teethcleaning, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vaccine, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(earcleaning, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nailtrim, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(55, 55, 55)
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkup, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(haircut, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(style, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fleaandtickdips, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vaccine, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkup, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teethcleaning, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(haircut, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(earcleaning, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(style, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nailtrim, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fleaandtickdips, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        roundedPanel3.setBackground(new java.awt.Color(33, 38, 48));

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("3. Visitation Date");

        Price.setForeground(new java.awt.Color(255, 255, 255));
        Price.setText("Price: ");

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Visit Date");

        SavingVisit.setBackground(new java.awt.Color(46, 115, 50));
        SavingVisit.setForeground(new java.awt.Color(255, 255, 255));
        SavingVisit.setText("Save Visit");
        SavingVisit.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(VisitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Price, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                    .addComponent(SavingVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(58, 58, 58))
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Price, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addGap(0, 17, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SavingVisit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(VisitDate, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Visit & Transaction Form");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roundedPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void SearchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchBarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SearchBarActionPerformed

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
                new VisitAndTransaction().setVisible(true);
            }
        });
        
        /* Create and display the form */
    //    java.awt.EventQueue.invokeLater(() -> new VisitAndTransaction().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private studentenrollmentsystem.RoundedButton Back;
    private design.RoundTextField Price;
    private studentenrollmentsystem.RoundedButton Refresh;
    private studentenrollmentsystem.RoundedButton SavingVisit;
    private design.RoundTextField SearchBar;
    private design.RoundTextField VisitDate;
    private design.RoundCheckBox checkup;
    private design.RoundCheckBox earcleaning;
    private design.RoundCheckBox fleaandtickdips;
    private design.RoundCheckBox haircut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private design.RoundCheckBox nailtrim;
    private design.RoundedPanel roundedPanel1;
    private design.RoundedPanel roundedPanel2;
    private design.RoundedPanel roundedPanel3;
    private design.RoundCheckBox style;
    private design.RoundCheckBox teethcleaning;
    private design.RoundCheckBox vaccine;
    // End of variables declaration//GEN-END:variables
}
