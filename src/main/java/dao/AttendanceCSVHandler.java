package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Attendance;

public class AttendanceCSVHandler implements AttendanceDAO {
    

    @Override
    public List<Attendance> getAttendanceByEmployee(int empNo) {
        List<Attendance> list = new ArrayList<>();

        String sql = "SELECT attendance_date, time_in, time_out FROM public.attendance WHERE employee_id = ? ORDER BY attendance_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, empNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("attendance_date");
                    if (sqlDate == null) continue;
                    
                    LocalDate date = sqlDate.toLocalDate();
                    
                    LocalTime tIn = rs.getTime("time_in") != null ? rs.getTime("time_in").toLocalTime() : null;
                    LocalTime tOut = rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null;
                    
                    list.add(new Attendance(empNo, date, tIn, tOut));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Attendance Error: " + e.getMessage());
        }
        return list;
    }

    
    @Override
    public Object[][] getAttendanceByMonth(int empNo, String month, String year) {
        List<Object[]> records = new ArrayList<>();
        boolean isAllMonths = month.equalsIgnoreCase("ALL");
        
        StringBuilder sql = new StringBuilder(
            "SELECT attendance_date, time_in::time(0), COALESCE(time_out::time(0)::text, '') " +
            "FROM public.attendance WHERE employee_id = ?"
        );

        if (!isAllMonths) {
            sql.append(" AND EXTRACT(MONTH FROM attendance_date) = ?");
        }
        sql.append(" AND EXTRACT(YEAR FROM attendance_date) = ? ORDER BY attendance_date ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            pstmt.setInt(1, empNo);
            int currentParam = 2; 
            
            if (!isAllMonths) {
                int mVal;
                try { 
                    mVal = Integer.parseInt(month.trim()); 
                } catch (NumberFormatException e) {
                    mVal = java.time.Month.valueOf(month.trim().toUpperCase()).getValue();
                }
                pstmt.setInt(currentParam++, mVal); 
            }
            
            pstmt.setInt(currentParam, Integer.parseInt(year.trim())); 

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(new Object[]{ rs.getString(1), rs.getString(2), rs.getString(3) });
                }
            }
        } catch (Exception e) {
            System.err.println("CRITICAL DAO ERROR: " + e.getMessage());
        }
        return records.toArray(new Object[0][0]);
    }

    @Override
    public void recordAttendance(int empNo, String lastName, String firstName, String type) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return;

            String cleanType = type.trim().replace(" ", "-");

            if (cleanType.equalsIgnoreCase("Check-in")) {
                String sql = "INSERT INTO public.attendance (employee_id, last_name, first_name, attendance_date, time_in) " +
                             "SELECT ?, ?, ?, CURRENT_DATE, CURRENT_TIME(0) " +
                             "WHERE NOT EXISTS (" +
                             "    SELECT 1 FROM public.attendance " +
                             "    WHERE employee_id = ? AND attendance_date = CURRENT_DATE" +
                             ")";
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, empNo);
                    pstmt.setString(2, lastName);
                    pstmt.setString(3, firstName);
                    pstmt.setInt(4, empNo); 
                    pstmt.executeUpdate();
                }
            } else {
                String sql = "UPDATE public.attendance SET time_out = CURRENT_TIME(0) " +
                             "WHERE employee_id = ? AND attendance_date = CURRENT_DATE AND time_out IS NULL";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, empNo);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public String getLastStatus(int empId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT time_out FROM public.attendance WHERE employee_id = ? AND attendance_date = CURRENT_DATE";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, empId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1) == null ? "Check-in" : "Check-out";
                    }
                }
            }
        } catch (SQLException e) { }
        return "Check-out";
    }

    @Override 
    public Map<String, Integer> countWorkingDaysPerMonth() {
        Map<String, Integer> report = new HashMap<>();
        String sql = "SELECT TO_CHAR(attendance_date, 'MM/YYYY') as my, COUNT(DISTINCT attendance_date) as days " +
                     "FROM public.attendance GROUP BY my";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                report.put(rs.getString("my"), rs.getInt("days"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return report;
    }



    @Override
    public List<model.LeaveRequest> findLeavesByEmployeeId(int empNo) {
        List<model.LeaveRequest> leaves = new ArrayList<>();
        String sql = "SELECT leave_type, start_date, end_date, status FROM public.leave_requests WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, empNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.LeaveRequest leave = new model.LeaveRequest();
                    leave.setLeaveType(rs.getString("leave_type"));
                    leave.setStartDate(rs.getDate("start_date").toLocalDate());
                    leave.setEndDate(rs.getDate("end_date").toLocalDate());
                    leave.setStatus(rs.getString("status"));
                    leaves.add(leave);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Leave Fetch Error: " + e.getMessage());
        }
        return leaves;
    }
}
