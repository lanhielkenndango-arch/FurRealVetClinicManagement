package furrealvetclinicmanagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseSetup {
    private static boolean prepared;

    private DatabaseSetup() {
    }

    public static void ensureTables() {
        if (prepared) {
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS clients (
                        client_id INT PRIMARY KEY,
                        first_name VARCHAR(80) NOT NULL,
                        last_name VARCHAR(80) NOT NULL,
                        phone VARCHAR(30) NOT NULL UNIQUE,
                        email VARCHAR(120) NOT NULL UNIQUE,
                        password_text VARCHAR(120) NOT NULL
                    )
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS pets (
                        pet_id INT AUTO_INCREMENT PRIMARY KEY,
                        client_id INT NOT NULL,
                        pet_name VARCHAR(80) NOT NULL,
                        pet_type VARCHAR(40) NOT NULL,
                        breed VARCHAR(80) NOT NULL,
                        age INT NOT NULL,
                        FOREIGN KEY (client_id) REFERENCES clients(client_id)
                            ON DELETE CASCADE ON UPDATE CASCADE
                    )
                    """);
            preparePetsTable(conn, stmt);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS clinic_services (
                        service_id INT AUTO_INCREMENT PRIMARY KEY,
                        service_name VARCHAR(100) NOT NULL,
                        category VARCHAR(60) NOT NULL,
                        price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                        service_date VARCHAR(40) NOT NULL DEFAULT ''
                    )
                    """);
            addColumnIfMissing(stmt, "clinic_services", "service_date",
                    "VARCHAR(40) NOT NULL DEFAULT ''");
            normalizeDateColumn(stmt, "clinic_services", "service_date");

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS visits (
                        visit_id INT AUTO_INCREMENT PRIMARY KEY,
                        client_id INT NOT NULL,
                        pet_id INT NOT NULL,
                        visit_date VARCHAR(40) NOT NULL,
                        status VARCHAR(30) NOT NULL DEFAULT 'Scheduled',
                        total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                        FOREIGN KEY (client_id) REFERENCES clients(client_id)
                            ON DELETE RESTRICT ON UPDATE CASCADE,
                        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
                            ON DELETE RESTRICT ON UPDATE CASCADE
                    )
                    """);
            prepareVisitsTable(conn, stmt);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS visit_services (
                        visit_service_id INT AUTO_INCREMENT PRIMARY KEY,
                        visit_id INT NOT NULL,
                        service_id INT NOT NULL,
                        quantity INT NOT NULL DEFAULT 1,
                        line_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                        FOREIGN KEY (visit_id) REFERENCES visits(visit_id)
                            ON DELETE CASCADE ON UPDATE CASCADE,
                        FOREIGN KEY (service_id) REFERENCES clinic_services(service_id)
                            ON DELETE RESTRICT ON UPDATE CASCADE
                    )
                    """);
            prepareVisitServicesTable(conn, stmt);

            removeInvalidRelationshipRows(stmt);
            removeLegacySchema(conn, stmt);
            ensureCurrentForeignKeys(conn, stmt);

            prepared = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addColumnIfMissing(Statement stmt, String tableName,
            String columnName, String definition) throws SQLException {
        try {
            stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
        } catch (SQLException e) {
            if (!"42S21".equals(e.getSQLState()) && e.getErrorCode() != 1060) {
                throw e;
            }
        }
    }

    private static void preparePetsTable(Connection conn, Statement stmt) throws SQLException {
        addColumnIfMissing(stmt, "pets", "client_id", "INT NULL");
        addColumnIfMissing(stmt, "pets", "pet_name", "VARCHAR(80) NOT NULL DEFAULT ''");
        addColumnIfMissing(stmt, "pets", "pet_type", "VARCHAR(40) NOT NULL DEFAULT ''");
        addColumnIfMissing(stmt, "pets", "age", "INT NOT NULL DEFAULT 0");

        if (columnExists(conn, "pets", "owner_id")) {
            dropForeignKeysForColumn(conn, stmt, "pets", "owner_id");
            makeColumnNullable(stmt, "pets", "owner_id", "INT");
            stmt.executeUpdate("UPDATE pets SET client_id = owner_id WHERE client_id IS NULL");
        }
        if (columnExists(conn, "pets", "name")) {
            makeColumnNullable(stmt, "pets", "name", "VARCHAR(255)");
            stmt.executeUpdate("""
                    UPDATE pets
                    SET pet_name = name
                    WHERE (pet_name IS NULL OR pet_name = '')
                      AND name IS NOT NULL
                    """);
        }
        if (columnExists(conn, "pets", "species")) {
            makeColumnNullable(stmt, "pets", "species", "VARCHAR(255)");
            stmt.executeUpdate("""
                    UPDATE pets
                    SET pet_type = species
                    WHERE (pet_type IS NULL OR pet_type = '')
                      AND species IS NOT NULL
                    """);
        }
        if (columnExists(conn, "pets", "date_of_birth")) {
            stmt.executeUpdate("""
                    UPDATE pets
                    SET age = GREATEST(TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()), 0)
                    WHERE age = 0
                      AND date_of_birth IS NOT NULL
                    """);
        }
    }

    private static void prepareVisitsTable(Connection conn, Statement stmt) throws SQLException {
        addColumnIfMissing(stmt, "visits", "client_id", "INT NULL");
        addColumnIfMissing(stmt, "visits", "total", "DECIMAL(10,2) NOT NULL DEFAULT 0.00");

        if (columnExists(conn, "visits", "user_id")) {
            dropForeignKeysForColumn(conn, stmt, "visits", "user_id");
            makeColumnNullable(stmt, "visits", "user_id", "INT");
            stmt.executeUpdate("UPDATE visits SET client_id = user_id WHERE client_id IS NULL");
        }
        if (columnExists(conn, "visits", "visit_date")) {
            stmt.executeUpdate("ALTER TABLE visits MODIFY COLUMN visit_date VARCHAR(40) NOT NULL");
            normalizeDateColumn(stmt, "visits", "visit_date");
        }
        if (columnExists(conn, "visits", "status")) {
            stmt.executeUpdate("ALTER TABLE visits MODIFY COLUMN status VARCHAR(30) NOT NULL DEFAULT 'Scheduled'");
        }
    }

    private static void prepareVisitServicesTable(Connection conn, Statement stmt) throws SQLException {
        ensureVisitServicesPrimaryKey(conn, stmt);
        addColumnIfMissing(stmt, "visit_services", "quantity", "INT NOT NULL DEFAULT 1");
        addColumnIfMissing(stmt, "visit_services", "line_total", "DECIMAL(10,2) NOT NULL DEFAULT 0.00");
        dropColumnIfExists(conn, stmt, "visit_services", "medical_notes");
    }

    private static void ensureVisitServicesPrimaryKey(Connection conn, Statement stmt) throws SQLException {
        if (!columnExists(conn, "visit_services", "visit_service_id")) {
            dropForeignKeysForColumn(conn, stmt, "visit_services", "visit_id");
            dropForeignKeysForColumn(conn, stmt, "visit_services", "service_id");
            dropPrimaryKeyIfExists(conn, stmt, "visit_services");
            stmt.executeUpdate("""
                    ALTER TABLE visit_services
                    ADD COLUMN visit_service_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST
                    """);
            return;
        }

        if (!primaryKeyExists(conn, "visit_services", "visit_service_id")) {
            dropForeignKeysForColumn(conn, stmt, "visit_services", "visit_id");
            dropForeignKeysForColumn(conn, stmt, "visit_services", "service_id");
            dropPrimaryKeyIfExists(conn, stmt, "visit_services");
            stmt.executeUpdate("ALTER TABLE visit_services ADD PRIMARY KEY (visit_service_id)");
        }
    }

    private static void removeInvalidRelationshipRows(Statement stmt) throws SQLException {
        stmt.executeUpdate("""
                DELETE vs FROM visit_services vs
                LEFT JOIN visits v ON vs.visit_id = v.visit_id
                WHERE v.visit_id IS NULL
                """);
        stmt.executeUpdate("""
                DELETE vs FROM visit_services vs
                LEFT JOIN clinic_services cs ON vs.service_id = cs.service_id
                WHERE cs.service_id IS NULL
                """);
        stmt.executeUpdate("""
                DELETE v FROM visits v
                LEFT JOIN clients c ON v.client_id = c.client_id
                WHERE c.client_id IS NULL
                """);
        stmt.executeUpdate("""
                DELETE v FROM visits v
                LEFT JOIN pets p ON v.pet_id = p.pet_id
                WHERE p.pet_id IS NULL
                """);
        stmt.executeUpdate("""
                DELETE p FROM pets p
                LEFT JOIN clients c ON p.client_id = c.client_id
                WHERE p.client_id IS NULL
                   OR c.client_id IS NULL
                """);
    }

    private static void removeLegacySchema(Connection conn, Statement stmt) throws SQLException {
        dropColumnIfExists(conn, stmt, "clients", "created_at");

        dropColumnIfExists(conn, stmt, "pets", "owner_id");
        dropColumnIfExists(conn, stmt, "pets", "name");
        dropColumnIfExists(conn, stmt, "pets", "species");
        dropColumnIfExists(conn, stmt, "pets", "date_of_birth");

        dropColumnIfExists(conn, stmt, "visits", "user_id");

        dropTableIfExists(stmt, "services");
        dropTableIfExists(stmt, "users");
        dropTableIfExists(stmt, "owners");
    }

    private static void ensureCurrentForeignKeys(Connection conn, Statement stmt) throws SQLException {
        ensureForeignKey(conn, stmt, "pets", "client_id", "clients", "client_id",
                "fk_pets_client", "ON DELETE CASCADE ON UPDATE CASCADE");
        ensureForeignKey(conn, stmt, "visits", "client_id", "clients", "client_id",
                "fk_visits_client", "ON DELETE RESTRICT ON UPDATE CASCADE");
        ensureForeignKey(conn, stmt, "visits", "pet_id", "pets", "pet_id",
                "fk_visits_pet", "ON DELETE RESTRICT ON UPDATE CASCADE");
        ensureForeignKey(conn, stmt, "visit_services", "visit_id", "visits", "visit_id",
                "fk_visit_services_visit", "ON DELETE CASCADE ON UPDATE CASCADE");
        ensureForeignKey(conn, stmt, "visit_services", "service_id", "clinic_services", "service_id",
                "fk_visit_services_service", "ON DELETE RESTRICT ON UPDATE CASCADE");
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), null, tableName, columnName)) {
            return rs.next();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, tableName, null)) {
            return rs.next();
        }
    }

    private static boolean primaryKeyExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), null, tableName)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void makeColumnNullable(Statement stmt, String tableName,
            String columnName, String type) throws SQLException {
        stmt.executeUpdate("ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " + type + " NULL");
    }

    private static void dropForeignKeysForColumn(Connection conn, Statement stmt,
            String tableName, String columnName) throws SQLException {
        List<String> foreignKeys = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getImportedKeys(conn.getCatalog(), null, tableName)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("FKCOLUMN_NAME"))) {
                    foreignKeys.add(rs.getString("FK_NAME"));
                }
            }
        }

        for (String foreignKey : foreignKeys) {
            stmt.executeUpdate("ALTER TABLE " + tableName + " DROP FOREIGN KEY " + quoteIdentifier(foreignKey));
        }
    }

    private static void dropColumnIfExists(Connection conn, Statement stmt,
            String tableName, String columnName) throws SQLException {
        if (!tableExists(conn, tableName) || !columnExists(conn, tableName, columnName)) {
            return;
        }

        dropForeignKeysForColumn(conn, stmt, tableName, columnName);
        stmt.executeUpdate("ALTER TABLE " + tableName + " DROP COLUMN " + quoteIdentifier(columnName));
    }

    private static void dropTableIfExists(Statement stmt, String tableName) throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS " + quoteIdentifier(tableName));
    }

    private static void dropPrimaryKeyIfExists(Connection conn, Statement stmt, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), null, tableName)) {
            if (rs.next()) {
                stmt.executeUpdate("ALTER TABLE " + quoteIdentifier(tableName) + " DROP PRIMARY KEY");
            }
        }
    }

    private static void ensureForeignKey(Connection conn, Statement stmt,
            String tableName, String columnName, String referencedTable, String referencedColumn,
            String constraintName, String actions) throws SQLException {
        if (foreignKeyExists(conn, tableName, columnName, referencedTable, referencedColumn)) {
            return;
        }

        dropForeignKeysForColumn(conn, stmt, tableName, columnName);
        stmt.executeUpdate("""
                ALTER TABLE %s
                ADD CONSTRAINT %s
                FOREIGN KEY (%s) REFERENCES %s(%s)
                %s
                """.formatted(
                        quoteIdentifier(tableName),
                        quoteIdentifier(constraintName),
                        quoteIdentifier(columnName),
                        quoteIdentifier(referencedTable),
                        quoteIdentifier(referencedColumn),
                        actions));
    }

    private static boolean foreignKeyExists(Connection conn, String tableName, String columnName,
            String referencedTable, String referencedColumn) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getImportedKeys(conn.getCatalog(), null, tableName)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("FKCOLUMN_NAME"))
                        && referencedTable.equalsIgnoreCase(rs.getString("PKTABLE_NAME"))
                        && referencedColumn.equalsIgnoreCase(rs.getString("PKCOLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String quoteIdentifier(String identifier) {
        return "`" + identifier.replace("`", "``") + "`";
    }

    private static void normalizeDateColumn(Statement stmt, String tableName,
            String columnName) throws SQLException {
        stmt.executeUpdate("""
                UPDATE %s
                SET %s = DATE_FORMAT(STR_TO_DATE(%s, '%%Y-%%m-%%d'), '%%m/%%d/%%Y')
                WHERE %s REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$'
                """.formatted(tableName, columnName, columnName, columnName));
    }
}
