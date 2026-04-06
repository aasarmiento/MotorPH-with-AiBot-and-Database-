package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import model.AccountingStaff;
import model.Admin;
import model.Employee;
import model.HRStaff;
import model.ITStaff;
import model.LeaveRequest;
import model.RegularStaff;
import model.Role;

public class CSVHandler implements EmployeeDAO {

    public CSVHandler() {
        // Database-only: No migration call here to prevent duplicate uploads
    }

    @Override 
    public List<Employee> getAll() { 
        return getAllEmployees(); 
    }

    @Override 
    public List<Employee> getAllEmployees() { 
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM public.employees ORDER BY employee_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) { System.err.println("DB Error: " + e.getMessage()); }
        return employees;
    }

    @Override 
    public Employee findById(int id) { 
        String sql = "SELECT * FROM public.employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToEmployee(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override 
public List<Employee> findAll() { 
    List<Employee> employees = new ArrayList<>();
    // Make sure your table name matches (public.employees)
    String sql = "SELECT * FROM public.employees ORDER BY employee_id ASC";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            // This uses your existing mapping logic to turn a row into an Employee object
            employees.add(mapResultSetToEmployee(rs));
        }
    } catch (SQLException e) { 
        e.printStackTrace(); 
    }
    return employees;
}

    @Override public Employee getById(int id) { return findById(id); }

    @Override 
    public Employee findByUsername(String u) { 
        String sql = "SELECT * FROM public.employees WHERE first_name ILIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToEmployee(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override 
    public boolean create(Employee emp) { 
        return addEmployee(emp, null); 
    }

    @Override
    public boolean addEmployee(Employee emp, byte[] photoBytes) {
        String sql = "INSERT INTO public.employees (employee_id, last_name, first_name, birthday, address, " +
                     "phone_number, sss_number, philhealth_number, tin_number, pagibig_number, status, position, " +
                     "immediate_supervisor, basic_salary, rice_subsidy, phone_allowance, clothing_allowance, " +
                     "gross_semimonthly_rate, hourly_rate, role, gender, profile_picture) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, emp.getEmpNo());
            pstmt.setString(2, emp.getLastName());
            pstmt.setString(3, emp.getFirstName());
            pstmt.setDate(4, java.sql.Date.valueOf(emp.getBirthday()));
            pstmt.setString(5, emp.getAddress());
            pstmt.setString(6, emp.getPhone());
            pstmt.setString(7, emp.getSss());
            pstmt.setString(8, emp.getPhilhealth());
            pstmt.setString(9, emp.getTin());
            pstmt.setString(10, emp.getPagibig());
            pstmt.setString(11, emp.getStatus());
            pstmt.setString(12, emp.getPosition());
            pstmt.setString(13, emp.getSupervisor());
            pstmt.setDouble(14, emp.getBasicSalary());
            pstmt.setDouble(15, emp.getRiceSubsidy());
            pstmt.setDouble(16, emp.getPhoneAllowance());
            pstmt.setDouble(17, emp.getClothingAllowance());
            
            pstmt.setDouble(18, emp.getBasicSalary() / 2); 
            pstmt.setDouble(19, emp.getBasicSalary() / 21 / 8);
            
            pstmt.setString(20, emp.getRole().name());
            pstmt.setString(21, emp.getGender());

            if (photoBytes != null) {
                pstmt.setBytes(22, photoBytes);
            } else {
                pstmt.setNull(22, java.sql.Types.BINARY);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
   
    @Override
    public int getNextAvailableId() {
        String sql = "SELECT MAX(employee_id) FROM public.employees";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1) + 1;
        } catch (SQLException e) { e.printStackTrace(); }
        return 10001; 
    }

    @Override
    public int getLastEmployeeNumber() {
        return getNextAvailableId() - 1;
    }

    @Override 
    public boolean update(Employee emp) { 
        String sql = "UPDATE public.employees SET last_name=?, first_name=?, address=?, phone_number=?, status=?, position=? WHERE employee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getLastName());
            pstmt.setString(2, emp.getFirstName());
            pstmt.setString(3, emp.getAddress());
            pstmt.setString(4, emp.getPhone());
            pstmt.setString(5, emp.getStatus());
            pstmt.setString(6, emp.getPosition());
            pstmt.setInt(7, emp.getEmpNo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    @Override 
    public boolean deleteEmployee(int empId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM public.attendance WHERE employee_id = ?")) {
                p1.setInt(1, empId); p1.executeUpdate();
            }
            try (PreparedStatement p2 = conn.prepareStatement("DELETE FROM public.employees WHERE employee_id = ?")) {
                p2.setInt(1, empId);
                if (p2.executeUpdate() > 0) { conn.commit(); return true; }
            }
            conn.rollback(); return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override public void unlockAccount(int id) { updateEmployeeStatus(id, "Active"); }

    @Override 
    public void updateEmployeeStatus(int id, String s) { 
        String sql = "UPDATE public.employees SET status = ? WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s); pstmt.setInt(2, id); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override 
    public void saveNewPassword(int id, String p) { 
        String sql = "UPDATE public.employees SET password = ? WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p); pstmt.setInt(2, id); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("employee_id");
        String ln = rs.getString("last_name");
        String fn = rs.getString("first_name");
        LocalDate bd = rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null;
        double bs = rs.getDouble("basic_salary");

        Employee emp = new RegularStaff(id, ln, fn, bd, bs);
        
        emp.setSss(rs.getString("sss_number"));           
        emp.setPhilhealth(rs.getString("philhealth_number")); 
        emp.setTin(rs.getString("tin_number"));           
        emp.setPagibig(rs.getString("pagibig_number"));   
        emp.setRiceSubsidy(rs.getDouble("rice_subsidy"));
        emp.setPhoneAllowance(rs.getDouble("phone_allowance"));
        emp.setClothingAllowance(rs.getDouble("clothing_allowance"));
        emp.setGrossSemiMonthlyRate(rs.getDouble("gross_semimonthly_rate")); 
        emp.setHourlyRate(rs.getDouble("hourly_rate"));
        emp.setGender(rs.getString("gender"));
        emp.setAddress(rs.getString("address"));
        emp.setPhone(rs.getString("phone_number"));
        emp.setStatus(rs.getString("status"));
        emp.setPosition(rs.getString("position"));
        emp.setSupervisor(rs.getString("immediate_supervisor"));

        String roleStr = rs.getString("role");
        if (roleStr != null) emp = upgradeEmployeeRole(emp, roleStr);
        return emp;
    }

    private Employee upgradeEmployeeRole(Employee o, String roleName) {
        String formattedRole = roleName.toUpperCase().trim().replace(" ", "_");
        Role role;
        try { role = Role.valueOf(formattedRole); } 
        catch (Exception e) { role = Role.REGULAR_STAFF; }

        Employee upgraded = switch (role) {
            case ADMIN -> new Admin(o.getEmpNo(), o.getLastName(), o.getFirstName(), o.getBirthday(), o.getBasicSalary(), o.getGender());
            case HR_STAFF -> new HRStaff(o.getEmpNo(), o.getLastName(), o.getFirstName(), o.getBirthday(), o.getBasicSalary(), o.getGender());
            case IT_STAFF -> new ITStaff(o.getEmpNo(), o.getLastName(), o.getFirstName(), o.getBirthday(), o.getBasicSalary(), o.getGender());
            case ACCOUNTING -> new AccountingStaff(o.getEmpNo(), o.getLastName(), o.getFirstName(), o.getBirthday(), o.getBasicSalary(), o.getGender());
            default -> o;
        };
        
        upgraded.setRole(role);
        upgraded.setSss(o.getSss());
        upgraded.setPhilhealth(o.getPhilhealth());
        upgraded.setTin(o.getTin());
        upgraded.setPagibig(o.getPagibig());
        upgraded.setStatus(o.getStatus());
        upgraded.setPosition(o.getPosition());
        upgraded.setSupervisor(o.getSupervisor());
        upgraded.setAddress(o.getAddress());
        upgraded.setPhone(o.getPhone());
        upgraded.setRiceSubsidy(o.getRiceSubsidy());
        upgraded.setPhoneAllowance(o.getPhoneAllowance());
        upgraded.setClothingAllowance(o.getClothingAllowance());
        upgraded.setGrossSemiMonthlyRate(o.getGrossSemiMonthlyRate());
        upgraded.setHourlyRate(o.getHourlyRate());
        
        return upgraded;
    }


@Override
public ImageIcon getEmployeePhoto(int id) {
    // SQL to get the photo bytes we just uploaded
    String sql = "SELECT profile_picture FROM public.employees WHERE employee_id = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                byte[] imgBytes = rs.getBytes("profile_picture");
                
                // If the DB has the photo, use it!
                if (imgBytes != null && imgBytes.length > 0) {
                    return new ImageIcon(imgBytes);
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Database Error: " + e.getMessage());
    }
    
    // FALLBACK: Only use a file if the Database is empty
    return new ImageIcon("/Users/abigail/MotorPhF/src/main/resources/profile_pics/default.png");
}



@Override
public ImageIcon getDashboardIcon(String iconName) {
    String sql = "SELECT icon_bytes FROM public.dashboard_assets WHERE icon_name = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, iconName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                byte[] bt = rs.getBytes("icon_bytes");
                if (bt != null && bt.length > 0) {
                    return new ImageIcon(bt);
                }
            }
        }
    } catch (Exception e) {
        System.err.println("Error loading icon '" + iconName + "': " + e.getMessage());
    }
    return null; // Returns null if not found, allowing the UI to show the fallback text
}
    @Override 
    public void saveProfilePicture(int id, File selectedFile) throws IOException { 
        // Read file into bytes and update Database directly
        byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
        String sql = "UPDATE public.employees SET profile_picture = ? WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBytes(1, imageBytes);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override 
    public boolean createLoginCredentials(int id, String u, String p, String r) { 
        String sql = "INSERT INTO public.login_credentials (employee_id, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, u);
            pstmt.setString(3, p);
            pstmt.setString(4, r);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override public File getEmployeePhotoFile(int id) { return null; }
    @Override public void applyForLeave(int id, String t, String s, String en, String r) {}
    @Override public Object[][] getLeaveStatusByEmpId(int id) { return new Object[0][0]; }
    @Override public Object[][] getAllLeaveRequests() { return new Object[0][0]; }
    @Override public void updateLeaveStatus(String id, String s) {}
    @Override public List<LeaveRequest> getAllLeaveRequestsList() { return new ArrayList<>(); }
}