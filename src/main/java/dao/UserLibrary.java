package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Employee;
import model.Role;


public class UserLibrary {
    private final EmployeeDAO employeeDAO;
    
    private static Role userRole;
    private static Employee loggedInEmployee;

    public UserLibrary(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

public boolean authenticate(String username, String password) {
    // 1. Log the attempt to the console
    System.out.println("Attempting login for: [" + username + "]");

    // SQL using TRIM for safety and ILIKE for case-insensitive usernames
    String sql = "SELECT * FROM public.login_credentials WHERE TRIM(username) ILIKE ? AND TRIM(password) = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, username.trim());
        pstmt.setString(2, password.trim());
        
        ResultSet rs = pstmt.executeQuery();
        
        // 2. We only call rs.next() ONCE
        if (rs.next()) {
            System.out.println("User found in Database!");
            
            int empId = rs.getInt("employee_id");
            
            // Fetch the full employee details from the employees table
            Employee emp = employeeDAO.findById(empId);
            
            if (emp != null) {
                userRole = determineRole(emp.getPosition()); 
                emp.setRole(userRole); 
                loggedInEmployee = emp;
                
                System.out.println("Login Success: " + emp.getFirstName() + " " + emp.getLastName());
                return true;
            } else {
                System.out.println("Error: Credentials valid, but Employee ID " + empId + " not found in employees table.");
            }
        } else {
            System.out.println("No user found with those credentials (check Username/Password in pgAdmin).");
        }
    } catch (SQLException e) {
        System.err.println("Database Error during authentication:");
        e.printStackTrace();
    }
    return false;
}


    public static void logout() {
        userRole = null;
        loggedInEmployee = null;
        System.out.println("Session Ended.");
    }

    public static Role getUserRole() {
        return userRole;
    }

    public static Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }
    
    public static boolean isLoggedIn() {
        return loggedInEmployee != null;
    }
public static void loginUser(Employee emp) {
    loggedInEmployee = emp;
    userRole = emp.getRole();
}
public Role determineRole(String positionText) {
    if (positionText == null) return Role.REGULAR_STAFF;
    
    String pos = positionText.toLowerCase();
    
    
    if (pos.contains("accounting") || pos.contains("account") || pos.contains("finance")) {
        return Role.ACCOUNTING;
    }
    
    
    if (pos.contains("it") || pos.contains("systems") || pos.contains("operations")) {
        return Role.IT_STAFF;
    }
    
    
    if (pos.contains("hr") || pos.contains("human resources")) {
        return Role.HR_STAFF;
    }

   
    if (pos.contains("admin") || pos.contains("chief") || pos.contains("executive") || pos.contains("manager")) {
        return Role.ADMIN;
    }
    
    return Role.REGULAR_STAFF; 
}

}