package furrealvetclinicmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ServiceDAO {
    public boolean addService(String serviceName, String category, double price, String serviceDate) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String query = """
                INSERT INTO clinic_services (service_name, category, price, service_date)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, serviceName);
            stmt.setString(2, category);
            stmt.setDouble(3, price);
            stmt.setString(4, serviceDate);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadServicesToTable(DefaultTableModel model, String searchText) {
        loadServicesToTable(model, null, searchText, "");
    }

    public void loadServicesToTable(DefaultTableModel model, List<Integer> serviceIds,
            String dateSearchText, String categoryFilter) {
        DatabaseSetup.ensureTables();
        model.setRowCount(0);
        if (serviceIds != null) {
            serviceIds.clear();
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        String dateSearch = dateSearchText == null ? "" : dateSearchText.trim();
        String category = categoryFilter == null ? "" : categoryFilter.trim();
        String categoryAlias = categoryAlias(category);
        String query = """
                SELECT service_id, service_name, category, price, service_date
                FROM clinic_services
                WHERE (? = '' OR service_date LIKE ?)
                  AND (? = '' OR category = ? OR category = ?)
                ORDER BY service_date, service_name
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dateSearch);
            stmt.setString(2, "%" + dateSearch + "%");
            stmt.setString(3, category);
            stmt.setString(4, category);
            stmt.setString(5, categoryAlias);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (serviceIds != null) {
                    serviceIds.add(rs.getInt("service_id"));
                }
                model.addRow(new Object[] {
                    rs.getString("service_name"),
                    rs.getString("category"),
                    String.format("Php %.2f", rs.getDouble("price")),
                    DateInputUtil.toDisplayDate(rs.getString("service_date"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateService(int serviceId, String serviceName, String category,
            double price, String serviceDate) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        String query = """
                UPDATE clinic_services
                SET service_name = ?, category = ?, price = ?, service_date = ?
                WHERE service_id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, serviceName);
            stmt.setString(2, category);
            stmt.setDouble(3, price);
            stmt.setString(4, serviceDate);
            stmt.setInt(5, serviceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteService(int serviceId) {
        DatabaseSetup.ensureTables();
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement visitServices = conn.prepareStatement(
                    "DELETE FROM visit_services WHERE service_id = ?");
                 PreparedStatement services = conn.prepareStatement(
                         "DELETE FROM clinic_services WHERE service_id = ?")) {
                visitServices.setInt(1, serviceId);
                visitServices.executeUpdate();

                services.setInt(1, serviceId);
                boolean deleted = services.executeUpdate() > 0;
                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String categoryAlias(String category) {
        if ("Vaccines".equalsIgnoreCase(category)) {
            return "Vaccine";
        }
        if ("Vaccine".equalsIgnoreCase(category)) {
            return "Vaccines";
        }
        if ("Surgery".equalsIgnoreCase(category)) {
            return "Sergery";
        }
        if ("Sergery".equalsIgnoreCase(category)) {
            return "Surgery";
        }
        return category;
    }
}
