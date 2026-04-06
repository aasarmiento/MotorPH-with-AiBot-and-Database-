package service;

import java.awt.Image;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import dao.AttendanceDAO;
import dao.EmployeeDAO;
import model.Attendance;
import model.Employee;
import model.IAdminOperations;
import model.PayrollBreakdown;
import model.PeriodSummary;
import model.RegularStaff;
import model.Role;


public class EmployeeManagementService {
    private final EmployeeDAO employeeDao;
    private final AttendanceDAO attendanceDao;

    public EmployeeManagementService(EmployeeDAO employeeDao, AttendanceDAO attendanceDao) {
        this.employeeDao = employeeDao;
        this.attendanceDao = attendanceDao;
    }

    public List<Employee> getAll() { 
        return employeeDao.getAll();
    }

    public String[] getFormattedDataForForm(Object[] raw) {
        String[] uiData = new String[21];
        if (raw == null) return uiData;

        try {
            uiData[0] = String.valueOf(raw[0]);  // ID
            uiData[1] = String.valueOf(raw[1]);  // Last Name
            uiData[2] = String.valueOf(raw[2]);  // First Name
            uiData[3] = (raw.length > 20 && raw[20] != null) ? String.valueOf(raw[20]) : "N/A"; 
            uiData[4] = String.valueOf(raw[3]);  // Birthday
            uiData[5] = String.valueOf(raw[4]);  // Address
            uiData[6] = String.valueOf(raw[5]);  // Phone
            uiData[7] = String.valueOf(raw[6]);  // SSS
            uiData[8] = String.valueOf(raw[7]);  // Philhealth
            uiData[9] = String.valueOf(raw[8]);  // TIN
            uiData[10] = String.valueOf(raw[9]); // Pag-ibig
            uiData[11] = String.valueOf(raw[10]); // Status
            uiData[12] = String.valueOf(raw[11]); // Position
            uiData[13] = String.valueOf(raw[12]); // Supervisor
            uiData[14] = String.valueOf(raw[13]); // Basic Salary
            uiData[15] = String.valueOf(raw[14]); // Rice Subsidy
            uiData[16] = String.valueOf(raw[15]); // Phone Allowance
            uiData[17] = String.valueOf(raw[16]); // Clothing Allowance
            uiData[18] = String.valueOf(raw[17]); // Gross Semi-monthly
            uiData[19] = String.valueOf(raw[18]); // Hourly Rate
            uiData[20] = String.valueOf(raw[19]); // Role
        } catch (Exception e) {
            System.err.println("Mapping Error at Service Layer: " + e.getMessage());
            for(int i=0; i<uiData.length; i++) if(uiData[i] == null) uiData[i] = "";
        }
        return uiData;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> all = new java.util.ArrayList<>(employeeDao.getAll());
        all.removeIf(e -> e.getEmpNo() == 0 || 
                          e.getLastName().equalsIgnoreCase("asdasdads") || 
                          e.getFirstName().equalsIgnoreCase("asdasd"));
        return all;
    }

    public Object[] getEmployeeDetailsForForm(int empId) {
        Employee emp = employeeDao.findById(empId);
        if (emp == null) return null;

        return new Object[] {
            emp.getEmpNo(), emp.getLastName(), emp.getFirstName(), emp.getBirthday(),
            emp.getAddress(), emp.getPhone(), emp.getSss(), emp.getPhilhealth(),
            emp.getTin(), emp.getPagibig(), emp.getStatus(), emp.getPosition(),
            emp.getSupervisor(), String.format("%.0f", emp.getBasicSalary()),
            String.format("%.0f", emp.getRiceSubsidy()), String.format("%.0f", emp.getPhoneAllowance()),
            String.format("%.0f", emp.getClothingAllowance()), 
            String.format("%.2f", emp.getGrossSemiMonthlyRate()), 
            String.format("%.2f", emp.getHourlyRate()),           
            emp.getRole(), emp.getGender()
        };
    }

    public boolean processNewHire(Employee actor, String fName, String lName, String sss, double salary) {
        if (!(actor instanceof IAdminOperations)) {
            showError("Access Denied: Only Admins can register employees.");
            return false;
        }
        Employee newEmp = new RegularStaff();
        newEmp.setFirstName(fName);
        newEmp.setLastName(lName);
        newEmp.setSss(sss);
        newEmp.setBasicSalary(salary);
        newEmp.setRiceSubsidy(1500);
        newEmp.setPhoneAllowance(500);
        newEmp.setClothingAllowance(1000);
        newEmp.setRole(Role.REGULAR_STAFF); 
return registerEmployee((IAdminOperations)actor, newEmp, null);
    }

   public boolean registerEmployee(model.IAdminOperations actor, model.Employee emp, byte[] photo) {
        if (actor == null || emp == null) return false;

        if (emp.getFirstName().trim().isEmpty() || emp.getLastName().trim().isEmpty()) {
            showError("First and Last names are required!");
            return false;
        }

        if (emp.getRole() == null) {
            Role assignedRole = mapPositionToRole(emp.getPosition());
            emp.setRole(assignedRole);
        }

        int nextId = employeeDao.getNextAvailableId();
        if (nextId <= 0) nextId = 10001; 
        emp.setEmpNo(nextId);

        // Calculations preserved from your original snippet
        double hourly = emp.getBasicSalary() / 21 / 8;
        emp.setHourlyRate(hourly);
        emp.setGrossRate(emp.getBasicSalary() + emp.getRiceSubsidy() + emp.getPhoneAllowance() + emp.getClothingAllowance());
        
        // Updated: Pass both the employee object AND the photo bytes to the DAO
        boolean empSaved = employeeDao.addEmployee(emp, photo);

        if (empSaved) {
            String fName = emp.getFirstName().trim();
            String lName = emp.getLastName().trim().replaceAll("\\s+", "");
            
            String generatedUsername = fName.substring(0, 1).toUpperCase() + 
                                     lName.substring(0, 1).toUpperCase() + 
                                     lName.substring(1).toLowerCase();
            
            return employeeDao.createLoginCredentials(
                emp.getEmpNo(), 
                generatedUsername, 
                "1234", 
                emp.getRole().name() 
            );
        }
        return false;
    }

    public boolean updateEmployeeFromForm(Employee actor, JTextField[] fields) {
        try {
            if (!(actor instanceof model.IAdminOperations)) { 
                showError("Access Denied."); 
                return false; 
            }
            
            String bdayText = fields[4].getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            LocalDate birthday = bdayText.contains("-") ? LocalDate.parse(bdayText) : LocalDate.parse(bdayText, formatter);

            String position = fields[12].getText().trim();
            String roleNameFromForm = fields[20].getText().trim(); 
            Role assignedRole;
            
            try {
                assignedRole = Role.valueOf(roleNameFromForm);
            } catch (IllegalArgumentException e) {
                assignedRole = Role.fromLabel(roleNameFromForm);
            }

            Employee emp = createEmployeeInstance(assignedRole.name());
            emp.setEmpNo(Integer.parseInt(fields[0].getText().trim()));
            emp.setLastName(fields[1].getText().trim());
            emp.setFirstName(fields[2].getText().trim());
            emp.setGender(fields[3].getText().trim()); 
            emp.setBirthday(birthday);
            emp.setAddress(fields[5].getText().trim());
            emp.setPhone(fields[6].getText().trim());
            emp.setSss(fields[7].getText().trim());
            emp.setPhilhealth(fields[8].getText().trim());
            emp.setTin(fields[9].getText().trim());
            emp.setPagibig(fields[10].getText().trim());
            emp.setStatus(fields[11].getText().trim());
            emp.setPosition(position);
            emp.setSupervisor(fields[13].getText().trim());
            emp.setRole(assignedRole);

            double basic = parseDouble(fields[14].getText());
            emp.setBasicSalary(basic);
            emp.setRiceSubsidy(parseDouble(fields[15].getText()));
            emp.setPhoneAllowance(parseDouble(fields[16].getText()));
            emp.setClothingAllowance(parseDouble(fields[17].getText()));
            
            emp.setGrossRate(emp.getBasicSalary() + emp.getRiceSubsidy() + emp.getPhoneAllowance() + emp.getClothingAllowance());
            emp.setHourlyRate(basic / 21 / 8); 

            boolean success = employeeDao.update(emp);
            if (success) {
                employeeDao.createLoginCredentials(emp.getEmpNo(), null, null, assignedRole.name());
            }
            return success;
        } catch (Exception e) {
            System.err.println("Update Error: " + e.getMessage());
            return false;
        }
    }



public PeriodSummary getPayrollForEmployee(int empNo, String month, String year) {
    // 1. Get Employee Data (The Multiplier)
    model.Employee emp = employeeDao.findById(empNo);
    if (emp == null) return new PeriodSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    // 2. Get Attendance Data (The Hours)
    Object[][] logs = attendanceDao.getAttendanceByMonth(empNo, month, year);
    
    double totalHours = 0;
    System.out.println("--- LOGS FOUND FOR " + month + "/" + year + ": " + logs.length + " rows ---");

    for (Object[] row : logs) {
        if (row[1] == null || row[2] == null || row[2].toString().isEmpty()) continue;

        LocalTime timeIn = parseFlexibleTime(row[1].toString());
        LocalTime timeOut = parseFlexibleTime(row[2].toString());

        if (timeIn != null && timeOut != null) {
            // Logic: Subtract 1 hour lunch only if they worked more than an hour
            long diffMins = java.time.Duration.between(timeIn, timeOut).toMinutes();
            double dailyHours = (diffMins > 60) ? (diffMins - 60) / 60.0 : diffMins / 60.0;
            
            if (dailyHours > 0) totalHours += dailyHours;
        }
    }

    // 3. Calculation via Policy
    DolePolicy policy = new DolePolicy();
    // Pass the correctly mapped basic_salary and hourly_rate from the DAO
    PayrollBreakdown breakdown = policy.compute(emp, totalHours, 0.0);

    return new PeriodSummary(
        breakdown.getGrossPay(), breakdown.getSss(), breakdown.getPhilhealth(),
        breakdown.getPagibig(), breakdown.getWithholdingTax(), breakdown.getNetPay()
    );
}



    public void recordTimeLog(int empNo, String type) {
        Employee emp = employeeDao.findById(empNo);
        String action = type.toLowerCase().contains("in") ? "Check-in" : "Check-out";
        attendanceDao.recordAttendance(empNo, (emp != null ? emp.getLastName() : "Unknown"), (emp != null ? emp.getFirstName() : "Unknown"), action);
    }

    public Object[][] getAttendanceLogs(int empNo, String month, String year) {
        return attendanceDao.getAttendanceByMonth(empNo, month, year);
    }

    public String[] getSupervisorsForPosition(String position) {
        if (position == null) return new String[]{"N/A"};
        switch (position) {
            case "Chief Operating Officer": case "Chief Finance Officer": case "Chief Marketing Officer": case "Account Manager": return new String[]{"Garcia, Manuel III"};
            case "IT Operations and Systems": case "HR Manager": return new String[]{"Lim, Antonio"};
            case "HR Team Leader": return new String[]{"Villanueva, Andrea Mae"};
            case "HR Rank and File": return new String[]{"San Jose, Brad"};
            case "Accounting Head": return new String[]{"Aquino, Bianca Sofia"};
            case "Payroll Manager": return new String[]{"Alvaro, Roderick"};
            case "Payroll Team Leader": case "Payroll Rank and File": return new String[]{"Salcedo, Anthony"};
            case "Account Team Leader": return new String[]{"Romualdez, Fredrick"};
            case "Account Rank and File": return new String[]{"Mata, Christian", "De Leon, Selena"};
            case "Sales & Marketing": case "Supply Chain and Logistics": case "Customer Service and Relations": return new String[]{"Reyes, Isabella"};
            default: return new String[]{"N/A"};
        }
    }

    private double parseDouble(String input) {
        try { return Double.parseDouble(input.trim().replace(",", "")); } catch (Exception e) { return 0.0; }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean deleteEmployee(IAdminOperations actor, int id) {
        if (actor == null) { showError("Access Denied."); return false; }
        if (id == 10001) { showError("Account protected."); return false; }
        return employeeDao.deleteEmployee(id);
    }

    public boolean[] getButtonStates(int empNo) {
        boolean[] states = {true, false}; 
        try (java.sql.Connection conn = dao.DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM public.attendance WHERE employee_id = ? AND attendance_date = CURRENT_DATE AND time_out IS NULL";
            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, empNo);
                try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) { states[0] = false; states[1] = true; }
                }
            }
        } catch (java.sql.SQLException e) { }
        return states;
    }

    public EmployeeDAO getEmployeeDao() { return employeeDao; }

    public int generateNextEmployeeId() { return employeeDao.getNextAvailableId(); }

    public void updateEmployeePhoto(Employee emp, File selectedFile) {
        try {
            employeeDao.saveProfilePicture(emp.getEmpNo(), selectedFile);
            String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
            emp.setPhotoPath(emp.getEmpNo() + extension);
            employeeDao.update(emp);
        } catch (Exception e) { showError(e.getMessage()); }
    }

    public double getTotalHoursForCurrentMonth(int empNo) {
        String currentMonth = String.format("%02d", java.time.LocalDate.now().getMonthValue());
        String currentYear = String.valueOf(java.time.LocalDate.now().getYear());
        return getTotalHoursWorkedForMonth(empNo, currentMonth, currentYear);
    }

private LocalTime parseFlexibleTime(String timeStr) {
    // 1. Basic Safety check
    if (timeStr == null || timeStr.trim().isEmpty() || timeStr.equalsIgnoreCase("N/A")) {
        return null;
    }

    try {
        // 2. Clean up SQL milliseconds (17:00:00.000 -> 17:00:00)
        String cleanedTime = timeStr.trim().split("\\.")[0];
        
        // 3. Handle HH:mm or HH:mm:ss automatically
        if (cleanedTime.length() <= 5) {
            return LocalTime.parse(cleanedTime, DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return LocalTime.parse(cleanedTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    } catch (Exception e) {
        System.err.println("Time Parsing Error for: " + timeStr);
        return null;
    }
}
 public double[] getStandardAllowances(String position) {
        if (position == null) return new double[]{1500, 500, 500};
        switch (position) {
            case "Chief Operating Officer":
            case "Chief Finance Officer":
            case "Chief Marketing Officer":
            case "Chief Executive Officer":
                return new double[]{1500, 2000, 1000};
            case "IT Operations and Systems":
            case "HR Manager":
            case "Payroll Manager":
            case "Accounting Head":
            case "Account Manager":
                return new double[]{1500, 1000, 1000};
            case "HR Team Leader":
            case "Payroll Team Leader":
            case "Account Team Leader":
                return new double[]{1500, 800, 800};
            default:
                return new double[]{1500, 500, 500};
        }
    }
public ImageIcon getEmployeePhoto(int empId, int width, int height) {
    // 1. Get the Image from the DAO (Database) instead of a File
    ImageIcon icon = employeeDao.getEmployeePhoto(empId);
    
    // 2. If the DAO returned an image (or the default)
    if (icon != null) {
        // 3. Scale it to the exact size the UI requested
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    return null; 
}

    public double getBasicSalaryForPosition(String position) {
        return 50000.00;
    }

    public Role mapPositionToRole(String position) { return Role.fromString(position); }

    public Employee createEmployeeInstance(String roleName) {
        Role role = Role.fromString(roleName);
        switch (role) {
            case ADMIN: return new model.Admin();
            case HR_STAFF: return new model.HRStaff();
            case IT_STAFF: return new model.ITStaff();
            case ACCOUNTING: return new model.AccountingStaff();
            default: return new model.RegularStaff();
        }
    }

    public boolean isPayrollPeriodInFuture(String month, String year) {
        try {
            int monthNumber = parseMonth(month);
            java.time.YearMonth selected = java.time.YearMonth.of(Integer.parseInt(year), monthNumber);
            return selected.isAfter(java.time.YearMonth.now());
        } catch (Exception e) { return false; }
    }

    public boolean isPayrollPeriodIncomplete(String month, String year) {
        try {
            int monthNumber = parseMonth(month);
            java.time.YearMonth selected = java.time.YearMonth.of(Integer.parseInt(year), monthNumber);
            if (!selected.equals(java.time.YearMonth.now())) return false;
            return java.time.LocalDate.now().isBefore(selected.atEndOfMonth());
        } catch (Exception e) { return false; }
    }

    private int parseMonth(String month) {
        try {
            return Integer.parseInt(month);
        } catch (NumberFormatException e) {
            return java.time.Month.valueOf(month.toUpperCase()).getValue();
        }
    }

    public double getTotalHoursWorkedForMonth(int empNo, String month, String year) {
        Object[][] logs = attendanceDao.getAttendanceByMonth(empNo, month, year);
        DateTimeFormatter slashFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        DateTimeFormatter dashFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        double totalHours = 0.0;

        for (Object[] row : logs) {
            try {
                if (row[0] == null || row[1] == null || row[2] == null) continue;
                String dateStr = row[0].toString().trim();
                LocalDate date = dateStr.contains("-") ? LocalDate.parse(dateStr, dashFormat) : LocalDate.parse(dateStr, slashFormat);
                
                LocalTime tIn = parseFlexibleTime(row[1].toString());
                LocalTime tOut = parseFlexibleTime(row[2].toString());

                if (tIn != null && tOut != null) {
                    Attendance record = new Attendance(empNo, date, tIn, tOut);
                    totalHours += record.getHoursWorked();
                }
            } catch (Exception e) { }
        }
        return totalHours;
    }
}