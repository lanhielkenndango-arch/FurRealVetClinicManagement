package furrealvetclinicmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class VisitDAO {
    public boolean addVisit(int clientId, int petId, String visitDate, Map<String, Double> services) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String visitQuery = """
                INSERT INTO visits (client_id, pet_id, visit_date, status, total)
                VALUES (?, ?, ?, 'Scheduled', ?)
                """;

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement visitStmt = conn.prepareStatement(visitQuery, Statement.RETURN_GENERATED_KEYS)) {
            double total = services.values().stream().mapToDouble(Double::doubleValue).sum();
            visitStmt.setInt(1, clientId);
            visitStmt.setInt(2, petId);
            visitStmt.setString(3, visitDate);
            visitStmt.setDouble(4, total);
            visitStmt.executeUpdate();

            ResultSet keys = visitStmt.getGeneratedKeys();
            if (!keys.next()) {
                    throw new SQLException("Visit insert did not return a generated key.");
            }

            int visitId = keys.getInt(1);
            for (Map.Entry<String, Double> service : services.entrySet()) {
                    int serviceId = findOrCreateService(conn, service.getKey(), service.getValue());
                    addVisitService(conn, visitId, serviceId, service.getValue());
                }
            }
            conn.commit();
            return true;
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

    public void loadVisitsToTable(DefaultTableModel model, List<Integer> visitIds,
            String keywordSearch, String dateSearch, String statusFilter) {
        DatabaseSetup.ensureTables();
        model.setRowCount(0);
        visitIds.clear();

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String keyword = keywordSearch == null ? "" : keywordSearch.trim();
        String date = dateSearch == null ? "" : dateSearch.trim();
        String status = statusFilter == null ? "" : statusFilter.trim();
        String keywordPattern = "%" + keyword + "%";
        String datePattern = "%" + date + "%";

        String query = """
                SELECT v.visit_id,
                       CONCAT(c.first_name, ' ', c.last_name) AS owner_name,
                       p.pet_name,
                       v.visit_date,
                       v.status,
                       v.total
                FROM visits v
                INNER JOIN clients c ON v.client_id = c.client_id
                INNER JOIN pets p ON v.pet_id = p.pet_id
                WHERE (? = ''
                       OR CAST(v.visit_id AS CHAR) LIKE ?
                       OR CAST(v.client_id AS CHAR) LIKE ?
                       OR c.first_name LIKE ?
                       OR c.last_name LIKE ?
                       OR CONCAT(c.first_name, ' ', c.last_name) LIKE ?
                       OR p.pet_name LIKE ?)
                  AND (? = '' OR v.visit_date LIKE ?)
                  AND (? = '' OR v.status = ?)
                ORDER BY v.visit_id DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, keyword);
            stmt.setString(2, keywordPattern);
            stmt.setString(3, keywordPattern);
            stmt.setString(4, keywordPattern);
            stmt.setString(5, keywordPattern);
            stmt.setString(6, keywordPattern);
            stmt.setString(7, keywordPattern);
            stmt.setString(8, date);
            stmt.setString(9, datePattern);
            stmt.setString(10, status);
            stmt.setString(11, status);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int visitId = rs.getInt("visit_id");
                visitIds.add(visitId);
                model.addRow(new Object[] {
                    visitId,
                    rs.getString("owner_name"),
                    rs.getString("pet_name"),
                    DateInputUtil.toDisplayDate(rs.getString("visit_date")),
                    rs.getString("status"),
                    String.format("Php %.2f", rs.getDouble("total"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadVisitServicesToTable(DefaultTableModel model, int visitId) {
        DatabaseSetup.ensureTables();
        model.setRowCount(0);

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String query = """
                SELECT cs.service_name, cs.category, vs.quantity, vs.line_total
                FROM visit_services vs
                INNER JOIN clinic_services cs ON vs.service_id = cs.service_id
                WHERE vs.visit_id = ?
                ORDER BY cs.service_name
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("service_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    String.format("Php %.2f", rs.getDouble("line_total"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateVisit(int visitId, String visitDate, String status) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String query = "UPDATE visits SET visit_date = ?, status = ? WHERE visit_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, visitDate);
            stmt.setString(2, status);
            stmt.setInt(3, visitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVisit(int visitId) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM visits WHERE visit_id = ?")) {
            stmt.setInt(1, visitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int findOrCreateService(Connection conn, String serviceName, double price) throws SQLException {
        String findQuery = "SELECT service_id FROM clinic_services WHERE service_name = ?";

        try (PreparedStatement findStmt = conn.prepareStatement(findQuery)) {
            findStmt.setString(1, serviceName);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("service_id");
            }
        }

        String insertQuery = "INSERT INTO clinic_services (service_name, category, price) VALUES (?, 'Visit', ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, serviceName);
            insertStmt.setDouble(2, price);
            insertStmt.executeUpdate();

            ResultSet keys = insertStmt.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }

    private void addVisitService(Connection conn, int visitId, int serviceId, double price) throws SQLException {
        String query = """
                INSERT INTO visit_services (visit_id, service_id, quantity, line_total)
                VALUES (?, ?, 1, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, visitId);
            stmt.setInt(2, serviceId);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
        }
    }
}
