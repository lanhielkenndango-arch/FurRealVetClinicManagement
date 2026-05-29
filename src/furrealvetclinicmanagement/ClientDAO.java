package furrealvetclinicmanagement;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class ClientDAO {
    private final SecureRandom random = new SecureRandom();

    public int addClient(Client client) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return -1;
        }

        int clientId = generateClientId(conn);
        String query = """
                INSERT INTO clients (client_id, first_name, last_name, phone, email, password_text)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getLastName());
            stmt.setString(4, client.getPhoneNumber());
            stmt.setString(5, client.getEmail());
            stmt.setString(6, client.getPassword());
            stmt.executeUpdate();
            return clientId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean authenticate(String identifier, String password) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String query = """
                SELECT client_id FROM clients
                WHERE (email = ? OR phone = ? OR REPLACE(phone, '-', '') = ?)
                  AND BINARY password_text = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            stmt.setString(3, PhoneNumberUtil.digitsOnly(identifier));
            stmt.setString(4, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clientNameExists(String firstName, String lastName) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String query = """
                SELECT client_id FROM clients
                WHERE BINARY first_name = ?
                  AND BINARY last_name = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadClientsToTable(DefaultTableModel model, String searchText) {
        DatabaseSetup.ensureTables();
        model.setRowCount(0);

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String search = searchText == null ? "" : searchText.trim();
        String query = """
                SELECT client_id, first_name, last_name, phone
                FROM clients
                WHERE ? = ''
                   OR CAST(client_id AS CHAR) LIKE ?
                ORDER BY client_id
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, search);
            stmt.setString(2, "%" + search + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getInt("client_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("phone")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateClient(int clientId, String firstName, String lastName, String phoneNumber) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String query = "UPDATE clients SET first_name=?, last_name=?, phone=? WHERE client_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, phoneNumber);
            stmt.setInt(4, clientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteClient(int clientId) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            deleteVisitServicesForClient(conn, clientId);
            deleteByClientId(conn, "visits", clientId);
            deleteByClientId(conn, "pets", clientId);
            int deletedClients = deleteByClientId(conn, "clients", clientId);

            conn.commit();
            return deletedClients > 0;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteVisitServicesForClient(Connection conn, int clientId) throws SQLException {
        String query = """
                DELETE visit_services
                FROM visit_services
                INNER JOIN visits ON visit_services.visit_id = visits.visit_id
                WHERE visits.client_id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            stmt.executeUpdate();
        }
    }

    private int deleteByClientId(Connection conn, String tableName, int clientId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE client_id=?")) {
            stmt.setInt(1, clientId);
            return stmt.executeUpdate();
        }
    }

    private int generateClientId(Connection conn) {
        for (int attempt = 0; attempt < 20; attempt++) {
            int candidate = 100000 + random.nextInt(900000);
            if (!clientIdExists(conn, candidate)) {
                return candidate;
            }
        }
        return 100000 + random.nextInt(900000);
    }

    private boolean clientIdExists(Connection conn, int clientId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT client_id FROM clients WHERE client_id=?")) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}
