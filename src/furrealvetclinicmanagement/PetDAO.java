package furrealvetclinicmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PetDAO {
    public void addPet(Pet pet) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String query = "INSERT INTO pets (client_id, pet_name, pet_type, breed, age) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pet.getClientId());
            stmt.setString(2, pet.getPetName());
            stmt.setString(3, pet.getPetType());
            stmt.setString(4, pet.getBreed());
            stmt.setInt(5, pet.getAge());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPetsToTable(DefaultTableModel model, List<Integer> petIds, int clientId, String searchText) {
        DatabaseSetup.ensureTables();
        model.setRowCount(0);
        petIds.clear();

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String search = searchText == null ? "" : searchText.trim();
        String pattern = "%" + search + "%";
        String query = """
                SELECT pet_id, pet_name, pet_type, breed, age
                FROM pets
                WHERE client_id = ?
                  AND (? = ''
                       OR pet_name LIKE ?
                       OR pet_type LIKE ?
                       OR breed LIKE ?
                       OR CAST(age AS CHAR) LIKE ?)
                ORDER BY pet_name
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.setString(2, search);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);
            stmt.setString(6, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                petIds.add(rs.getInt("pet_id"));
                model.addRow(new Object[] {
                    rs.getString("pet_name"),
                    rs.getString("pet_type"),
                    rs.getString("breed"),
                    rs.getInt("age")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAllPetsToTable(DefaultTableModel model, List<Integer> petIds,
            List<Integer> clientIds, String searchText) {
        DatabaseSetup.ensureTables();
        model.setRowCount(0);
        petIds.clear();
        clientIds.clear();

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String search = searchText == null ? "" : searchText.trim();
        String query = """
                SELECT p.pet_id, p.client_id, p.pet_name, p.pet_type, p.breed, p.age
                FROM pets p
                INNER JOIN clients c ON p.client_id = c.client_id
                WHERE p.client_id IS NOT NULL
                  AND (? = ''
                       OR BINARY c.first_name LIKE ?
                       OR BINARY c.last_name LIKE ?
                       OR BINARY CONCAT(c.first_name, ' ', c.last_name) LIKE ?
                       OR CAST(p.client_id AS CHAR) LIKE ?)
                ORDER BY p.client_id, p.pet_name
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String pattern = "%" + search + "%";
            stmt.setString(1, search);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                petIds.add(rs.getInt("pet_id"));
                clientIds.add(rs.getInt("client_id"));
                model.addRow(new Object[] {
                    rs.getString("pet_name"),
                    rs.getString("pet_type"),
                    rs.getString("breed"),
                    rs.getInt("age")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePet(int petId, String petName, String petType, String breed, int age) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String query = "UPDATE pets SET pet_name=?, pet_type=?, breed=?, age=? WHERE pet_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, petName);
            stmt.setString(2, petType);
            stmt.setString(3, breed);
            stmt.setInt(4, age);
            stmt.setInt(5, petId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePet(int petId) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM pets WHERE pet_id=?")) {
            stmt.setInt(1, petId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
