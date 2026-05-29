/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package furrealvetclinicmanagement;

/**
 *
 * @author Asus
 */
public class FurRealVetClinicManagement {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
       // Activates FlatLaf immediately 
    com.formdev.flatlaf.FlatLightLaf.setup();
    DatabaseSetup.ensureTables();

    /* ... NetBeans auto-generated code ... */
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new Login().setVisible(true);
        }
    });
        
        
        
    }
    
}
