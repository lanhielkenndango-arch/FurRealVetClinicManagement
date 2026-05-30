/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package furrealvetclinicmanagement;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Asus
 */
public class ClientPet extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ClientPet.class.getName());
    private final ClientDAO clientDAO = new ClientDAO();
    private final PetDAO petDAO = new PetDAO();
    private final List<Integer> petIds = new ArrayList<>();
    private final List<Integer> petClientIds = new ArrayList<>();
    private int selectedClientId = -1;
    private String selectedOwnerName = "";

    /**
     * Creates new form ClientPet
     */
    public ClientPet() {
        initComponents();
        
        setHighQualityImage();
        
        design.DarkTableStyler.apply(jTable1, jScrollPane1);
        design.DarkTableStyler.apply(jTable2, jScrollPane2);
        setupLogic();
        TextFieldFocusUtil.install(getContentPane());
        
        this.setLocationRelativeTo(null);
    }

    private void setHighQualityImage() {
    try {
        // 1. Load the image directly from the package
        java.net.URL imgURL = getClass().getResource("/furrealvetclinicmanagement/mpaw.png");  
        if (imgURL == null) {
            System.err.println("Could not find image file!");
            return;
        }
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image originalImg = originalIcon.getImage();

        // 2. Use SCALE_SMOOTH to keep text and fine details razor sharp
        Image scaledImg = originalImg.getScaledInstance(64, 64, Image.SCALE_SMOOTH);

        // 3. Apply it to your label
        paw.setIcon(new ImageIcon(scaledImg)); 
        paw.revalidate();
        paw.repaint();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void setupLogic() {
        ComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
            "Dog", "Cat", "Bird", "Rabbit", "Hamster", "Other"
        }));
        jTable1.setDefaultEditor(Object.class, null);
        jTable2.setDefaultEditor(Object.class, null);
        installSelectionClearer(getContentPane());

        Add.addActionListener(evt -> addPet());
        Add1.addActionListener(evt -> refreshAll());
        paw.addActionListener(evt -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        TextPlaceholderUtil.applyPlaceholder(SearchByID, "ID/Phone");
        TextPlaceholderUtil.applyPlaceholder(SearchOwner, "First/Last Name");
        addSearchListener(SearchByID, this::loadClients);
        addSearchListener(SearchOwner, this::loadClients);

        jTable1.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                ownerSelected();
            }
        });

        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = jTable1.rowAtPoint(evt.getPoint());
                if (row < 0) {
                    clearTableHighlights();
                    return;
                }

                jTable2.clearSelection();
                if (evt.getClickCount() == 2) {
                    jTable1.setRowSelectionInterval(row, row);
                    showOwnerActions();
                    evt.consume();
                }
            }
        });

        jTable2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = jTable2.rowAtPoint(evt.getPoint());
                if (row < 0) {
                    clearTableHighlights();
                    return;
                }

                jTable1.clearSelection();
                if (evt.getClickCount() == 2) {
                    jTable2.setRowSelectionInterval(row, row);
                    showPetActions();
                    evt.consume();
                }
            }
        });

        loadClients();
    }

    private void refreshAll() {
        selectedClientId = -1;
        selectedOwnerName = "";
        OwnerName.setText("Add new pet for");
        clearTableHighlights();
        clearPetForm();
        TextPlaceholderUtil.resetPlaceholder(SearchByID);
        TextPlaceholderUtil.resetPlaceholder(SearchOwner);
        loadClients();
    }

    private void installSelectionClearer(Component component) {
        if (component == jTable1 || component == jTable2) {
            return;
        }

        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                clearTableHighlights();
            }
        });

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                installSelectionClearer(child);
            }
        }
    }

    private void clearTableHighlights() {
        jTable1.clearSelection();
        jTable2.clearSelection();
    }

    private void addSearchListener(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        });
    }

    private void loadClients() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        String tableSearch = ValidationUtil.clean(SearchByID.getText(), "ID/Phone");
        String ownerSearch = ValidationUtil.clean(SearchOwner.getText(), "First/Last Name");
        if (ValidationUtil.hasBlank(tableSearch) && !ValidationUtil.hasBlank(ownerSearch)) {
            clientDAO.loadClientsByOwnerNameToTable(model, ownerSearch);
        } else {
            clientDAO.loadClientsToTable(model, tableSearch);
        }
        selectedClientId = -1;
        selectedOwnerName = "";
        OwnerName.setText("Add new pet for");
        clearPetForm();
        loadPets();
    }

    private void ownerSelected() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }

        int modelRow = jTable1.convertRowIndexToModel(row);
        selectedClientId = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString());
        String firstName = jTable1.getModel().getValueAt(modelRow, 1).toString();
        String lastName = jTable1.getModel().getValueAt(modelRow, 2).toString();
        selectedOwnerName = (firstName + " " + lastName).trim();
        OwnerName.setText("Add new pet for " + selectedOwnerName);
        clearPetForm();
        loadPets();
    }

    private void loadPets() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        String searchText = ValidationUtil.clean(SearchOwner.getText(), "First/Last Name");
        if (selectedClientId == -1) {
            petDAO.loadAllPetsToTable(model, petIds, petClientIds, searchText);
            return;
        }

        petClientIds.clear();
        petDAO.loadPetsToTable(model, petIds, selectedClientId, "");
    }

    private void clearPets() {
        ((DefaultTableModel) jTable2.getModel()).setRowCount(0);
        petIds.clear();
        petClientIds.clear();
    }

    private void clearPetForm() {
        PetTxtField.setText("");
        BreedTxtField.setText("");
        AgeTxtField.setText("");
        if (ComboBox.getItemCount() > 0) {
            ComboBox.setSelectedIndex(0);
        }
    }

    private void addPet() {
        if (selectedClientId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an owner first.");
            return;
        }

        String petName = ValidationUtil.clean(PetTxtField.getText(), "Pet Name");
        String breed = ValidationUtil.clean(BreedTxtField.getText(), "Breed");
        String type = ComboBox.getSelectedItem() == null ? "" : ComboBox.getSelectedItem().toString();
        Integer age = ValidationUtil.parseAge(AgeTxtField.getText());

        if (ValidationUtil.hasBlank(petName, breed, type) || age == null) {
            JOptionPane.showMessageDialog(this, "Please complete the pet name, type, breed, and age.");
            return;
        }

        petDAO.addPet(new Pet(selectedClientId, petName, type, breed, age));
        clearPetForm();
        loadPets();
    }

    private void showOwnerActions() {
        int row = jTable1.getSelectedRow();
        int modelRow = jTable1.convertRowIndexToModel(row);
        int clientId = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString());
        String firstName = jTable1.getModel().getValueAt(modelRow, 1).toString();
        String lastName = jTable1.getModel().getValueAt(modelRow, 2).toString();
        String phone = jTable1.getModel().getValueAt(modelRow, 3).toString();

        Object[] choices = {"Update", "Delete", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an action for " + firstName + ".",
                "Owner Actions", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, choices, choices[0]);

        if (choice == 0) {
            updateOwner(clientId, firstName, lastName, phone);
        } else if (choice == 1) {
            deleteOwner(clientId, firstName + " " + lastName);
        }
    }

    private void updateOwner(int clientId, String firstName, String lastName, String phone) {
        JTextField clientIdField = new JTextField(String.valueOf(clientId));
        JTextField firstNameField = new JTextField(firstName);
        JTextField lastNameField = new JTextField(lastName);
        JTextField phoneField = new JTextField(phone);
        clientIdField.setEditable(false);
        clientIdField.setFocusable(false);
        PhoneNumberUtil.applyPhoneMask(phoneField);
        Object[] fields = {
            "Client ID", clientIdField,
            "First Name", firstNameField,
            "Last Name", lastNameField,
            "Phone", phoneField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Update Owner", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newFirstName = firstNameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        String newPhone = PhoneNumberUtil.format(phoneField.getText());
        if (ValidationUtil.hasBlank(newFirstName, newLastName, newPhone)) {
            JOptionPane.showMessageDialog(this, "Please complete all owner details.");
            return;
        }

        if (!PhoneNumberUtil.isValidPhilippineMobile(newPhone)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number using 09##-###-####.");
            return;
        }

        if (clientDAO.clientNameExistsForOtherClient(clientId, newFirstName, newLastName)) {
            JOptionPane.showMessageDialog(this,
                    "Another owner already uses the exact same first name and last name.");
            return;
        }

        if (clientDAO.clientPhoneExistsForOtherClient(clientId, newPhone)) {
            JOptionPane.showMessageDialog(this, "Another owner already uses this phone number.");
            return;
        }

        if (clientDAO.updateClient(clientId, newFirstName, newLastName, newPhone)) {
            JOptionPane.showMessageDialog(this, "Owner details updated.");
            loadClients();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not update owner. The owner name or phone number may already be used.",
                    "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOwner(int clientId, String name) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + name + "? This removes the account, pets, and visit records.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (clientDAO.deleteClient(clientId)) {
                JOptionPane.showMessageDialog(this, "Owner account deleted.");
                loadClients();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not delete this owner account.",
                        "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showPetActions() {
        int row = jTable2.getSelectedRow();
        int modelRow = jTable2.convertRowIndexToModel(row);
        int petId = petIds.get(modelRow);
        String petName = jTable2.getModel().getValueAt(modelRow, 0).toString();
        String type = jTable2.getModel().getValueAt(modelRow, 1).toString();
        String breed = jTable2.getModel().getValueAt(modelRow, 2).toString();
        String age = jTable2.getModel().getValueAt(modelRow, 3).toString();

        Object[] choices = {"Update", "Delete", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an action for " + petName + ".",
                "Pet Actions", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, choices, choices[0]);

        if (choice == 0) {
            updatePet(petId, petName, type, breed, age);
        } else if (choice == 1) {
            deletePet(petId, petName);
        }
    }

    private void updatePet(int petId, String petName, String type, String breed, String age) {
        JTextField petNameField = new JTextField(petName);
        JTextField breedField = new JTextField(breed);
        JTextField ageField = new JTextField(age);
        javax.swing.JComboBox<String> typeBox = new javax.swing.JComboBox<>(
                new String[] {"Dog", "Cat", "Bird", "Rabbit", "Hamster", "Other"});
        typeBox.setSelectedItem(type);

        Object[] fields = {"Pet Name", petNameField, "Type", typeBox, "Breed", breedField, "Age", ageField};
        int result = JOptionPane.showConfirmDialog(this, fields, "Update Pet", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newPetName = petNameField.getText().trim();
        String newType = typeBox.getSelectedItem().toString();
        String newBreed = breedField.getText().trim();
        Integer newAge = ValidationUtil.parseAge(ageField.getText());
        if (ValidationUtil.hasBlank(newPetName, newType, newBreed) || newAge == null) {
            JOptionPane.showMessageDialog(this, "Please complete all pet details.");
            return;
        }

        petDAO.updatePet(petId, newPetName, newType, newBreed, newAge);
        loadPets();
    }

    private void deletePet(int petId, String petName) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + petName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            petDAO.deletePet(petId);
            loadPets();
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
        jLabel1 = new javax.swing.JLabel();
        SearchByID = new design.RoundTextField();
        jLabel4 = new javax.swing.JLabel();
        Add1 = new studentenrollmentsystem.RoundedButton();
        roundedPanel2 = new design.RoundedPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SearchOwner = new design.RoundTextField();
        roundedPanel3 = new design.RoundedPanel();
        OwnerName = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        PetTxtField = new design.RoundTextField();
        jLabel6 = new javax.swing.JLabel();
        BreedTxtField = new design.RoundTextField();
        jLabel7 = new javax.swing.JLabel();
        ComboBox = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        AgeTxtField = new design.RoundTextField();
        Add = new studentenrollmentsystem.RoundedButton();
        paw = new design.SleekIconButton();
        roundedLabel1 = new design.RoundedLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(24, 28, 36));

        roundedPanel1.setBackground(new java.awt.Color(33, 38, 48));
        roundedPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jTable1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Phone"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Owners");

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Search:");

        Add1.setBackground(new java.awt.Color(26, 31, 41));
        Add1.setForeground(new java.awt.Color(255, 255, 255));
        Add1.setText("Refresh");
        Add1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(99, 99, 99)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchByID, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Add1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchByID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Add1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        roundedPanel2.setBackground(new java.awt.Color(33, 38, 48));
        roundedPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jTable2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jTable2.setForeground(new java.awt.Color(255, 255, 255));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Pets");

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Owner:");

        SearchOwner.setForeground(new java.awt.Color(255, 255, 255));

        roundedPanel3.setBackground(new java.awt.Color(24, 28, 36));

        OwnerName.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        OwnerName.setForeground(new java.awt.Color(255, 255, 255));
        OwnerName.setText("Add new pet for");

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Pet Name");

        PetTxtField.setForeground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Breed");

        BreedTxtField.setForeground(new java.awt.Color(255, 255, 255));
        BreedTxtField.addActionListener(this::BreedTxtFieldActionPerformed);

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Type");

        ComboBox.setBackground(new java.awt.Color(26, 31, 41));
        ComboBox.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        ComboBox.setForeground(new java.awt.Color(255, 255, 255));
        ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pet Type", " " }));
        ComboBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Age");

        AgeTxtField.setForeground(new java.awt.Color(255, 255, 255));
        AgeTxtField.addActionListener(this::AgeTxtFieldActionPerformed);

        Add.setBackground(new java.awt.Color(26, 31, 41));
        Add.setForeground(new java.awt.Color(255, 255, 255));
        Add.setText("Add");
        Add.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundedPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(100, 100, 100))
                            .addGroup(roundedPanel3Layout.createSequentialGroup()
                                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PetTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BreedTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(56, 56, 56)
                                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboBox, 0, 150, Short.MAX_VALUE)
                                    .addComponent(AgeTxtField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 38, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(OwnerName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(OwnerName, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Add, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PetTxtField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AgeTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BreedTxtField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundedPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchOwner, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SearchOwner, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        paw.setText("sleekIconButton1");

        roundedLabel1.setForeground(new java.awt.Color(255, 255, 255));
        roundedLabel1.setText("Client & Pet Manager");
        roundedLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(paw, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(roundedLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paw, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundedLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundedPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
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

    private void AgeTxtFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgeTxtFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AgeTxtFieldActionPerformed

    private void BreedTxtFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BreedTxtFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BreedTxtFieldActionPerformed

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
                new ClientPet().setVisible(true);
            }
        });
        
        /* Create and display the form */
     //   java.awt.EventQueue.invokeLater(() -> new ClientPet().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private studentenrollmentsystem.RoundedButton Add;
    private studentenrollmentsystem.RoundedButton Add1;
    private design.RoundTextField AgeTxtField;
    private design.RoundTextField BreedTxtField;
    private javax.swing.JComboBox<String> ComboBox;
    private javax.swing.JLabel OwnerName;
    private design.RoundTextField PetTxtField;
    private design.RoundTextField SearchByID;
    private design.RoundTextField SearchOwner;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private design.SleekIconButton paw;
    private design.RoundedLabel roundedLabel1;
    private design.RoundedPanel roundedPanel1;
    private design.RoundedPanel roundedPanel2;
    private design.RoundedPanel roundedPanel3;
    // End of variables declaration//GEN-END:variables
}
