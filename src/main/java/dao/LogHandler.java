package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class LogHandler {
    
    private static final String LOGIN_CSV_PATH = "MotorPH_EmployeeLogin.csv";

    public String[] verifyCredentials(String username, String password) {


        String sql = "SELECT employee_id, first_name, role FROM public.employees WHERE (first_name = ? OR employee_id::text = ?) AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, username.trim()); 
            pstmt.setString(3, password.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {


                    return new String[] {
                        String.valueOf(rs.getInt("employee_id")),
                        rs.getString("first_name"),
                        "********", 
                        rs.getString("role")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Login Error: " + e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_CSV_PATH))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    if (data[1].equals(username) && data[2].equals(password)) {
                        return data; 
                    }
                }
            }
        } catch (IOException e) {
        }
        
        return null; 
    }

    public void logAction(String message) {

        System.out.println("[" + LocalDateTime.now() + "] LOG: " + message);
    }
}