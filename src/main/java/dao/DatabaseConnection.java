package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/MotorPH_Payroll";
    private static final String USER = "postgres";
    private static final String PASS = "no8"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found. Check your pom.xml!", e);
        }
    }

    public static void logEvent(int employeeId, String event) {
    String sql = "INSERT INTO public.audit_logs (log_timestamp, event_description, employee_id) VALUES (NOW(), ?, ?)";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, event);
        pstmt.setInt(2, employeeId); 
        pstmt.executeUpdate();
        
    } catch (SQLException e) {
        System.err.println("Audit Log Error: " + e.getMessage());
    }
}
}
