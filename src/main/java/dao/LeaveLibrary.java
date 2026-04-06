package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeaveLibrary {


    public List<String[]> fetchAllLeaves() {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT request_id, employee_id, last_name, first_name, leave_type, start_date, end_date, reason, status " +
                     "FROM public.leave_requests ORDER BY start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String[] row = new String[9];
                row[0] = rs.getString("request_id");
                row[1] = String.valueOf(rs.getInt("employee_id"));
                row[2] = rs.getString("last_name");
                row[3] = rs.getString("first_name");
                row[4] = rs.getString("leave_type");
                row[5] = String.valueOf(rs.getDate("start_date")); 
                row[6] = String.valueOf(rs.getDate("end_date"));  
                row[7] = rs.getString("reason");
                row[8] = rs.getString("status");
                data.add(row);
            }
        } catch (SQLException e) {
            System.err.println("DB Fetch Error: " + e.getMessage());
        }
        return data;
    }

    public boolean updateLeaveStatus(String requestId, String newStatus) {
        String sql = "UPDATE public.leave_requests SET status = ? WHERE request_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setString(2, requestId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Update Error: " + e.getMessage());
            return false;
        }
    }

    
    public boolean saveLeave(String requestId, int empId, String lastName, String firstName, 
                             String type, String start, String end, String reason, String status) {
                             
        String sql = "INSERT INTO public.leave_requests (request_id, employee_id, last_name, first_name, " +
                     "leave_type, start_date, end_date, reason, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?::date, ?::date, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, requestId);
            pstmt.setInt(2, empId);
            pstmt.setString(3, lastName);
            pstmt.setString(4, firstName);
            pstmt.setString(5, type);
            pstmt.setString(6, start); 
            pstmt.setString(7, end);   
            pstmt.setString(8, reason);
            pstmt.setString(9, status);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Save Error: " + e.getMessage());
            return false;
        }
    }


    public List<String[]> fetchAllLeavesFromFile() {
        return fetchAllLeaves();
    }

    public void saveLeave(String csvLine) {
        String[] p = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        if (p.length >= 9) {
            saveLeave(
                p[0].replace("\"", "").trim(),
                Integer.parseInt(p[1].replace("\"", "").trim()),
                p[2].replace("\"", "").trim(),
                p[3].replace("\"", "").trim(),
                p[4].replace("\"", "").trim(),
                p[5].replace("\"", "").trim(),
                p[6].replace("\"", "").trim(),
                p[7].replace("\"", "").trim(),
                p[8].replace("\"", "").trim()
            );
        }
    }
}