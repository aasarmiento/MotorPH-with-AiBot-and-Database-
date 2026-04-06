package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import dao.AttendanceDAO;
import dao.EmployeeDAO;
import dao.LeaveLibrary;
import model.Employee;

public class LeaveService {
    private final EmployeeDAO employeeDao;
    private final AttendanceDAO attendanceDao;
    private final LeaveLibrary leaveLibrary; 
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    private static final int MAX_VACATION = 15;
    private static final int MAX_SICK = 15; 
    private static final int MAX_EMERGENCY = 5; 
    private static final int MAX_MATERNITY = 105; 
    private static final int MAX_PATERNITY = 7;   

    public LeaveService(EmployeeDAO employeeDao, AttendanceDAO attendanceDao) {
        this.employeeDao = employeeDao;
        this.attendanceDao = attendanceDao;
        this.leaveLibrary = new LeaveLibrary();
    }

    public Object[][] getAttendanceByMonth(int empNo, String month, String year) {
    
    return attendanceDao.getAttendanceByMonth(empNo, month, year);
}
public void submitLeave(int empNo, String type, LocalDate start, LocalDate end, String reason) {
    
    if (end.isBefore(start)) {
        throw new IllegalArgumentException("Error: End date cannot be before start date.");
    }

    
    if (!start.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Submission Error: Leave requests must be submitted at least 1 day in advance. " +
                                           "The earliest available start date is " + LocalDate.now().plusDays(1).format(formatter) + ".");
    }

   
    long requestedDays = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    
    
    int currentBalance = getRemainingBalance(empNo, type);

    if (requestedDays > currentBalance) {
        throw new IllegalArgumentException("Insufficient Balance! You requested " + requestedDays + 
                                           " days, but you only have " + currentBalance + " days left for " + type + ".");
    }

    Employee emp = employeeDao.findById(empNo);
    if (emp == null) throw new RuntimeException("Employee not found.");

    List<String[]> allLeaves = leaveLibrary.fetchAllLeaves();
    int count = 1; 
    for (String[] row : allLeaves) {
        if (row.length > 1 && row[1].equals(String.valueOf(empNo))) {
            count++;
        }
    }
    
    String sequence = String.format("%02d", count);
    String generatedID = "LR-" + empNo + "-" + sequence;

    String startStr = start.format(formatter);  
    String endStr = end.format(formatter);
    String cleanReason = (reason == null || reason.trim().isEmpty()) ? "No reason" : "\"" + reason.replace("\"", "'") + "\"";

    String csvLine = String.format("%s,%d,%s,%s,%s,%s,%s,%s,PENDING",
            generatedID, empNo, emp.getLastName(), emp.getFirstName(), 
            type, startStr, endStr, cleanReason);

    leaveLibrary.saveLeave(csvLine);
}
    public Object[][] getLeaveHistory(int targetEmpId) {
        List<String[]> allLeaves = leaveLibrary.fetchAllLeaves();
        List<Object[]> filtered = new ArrayList<>();

        for (String[] row : allLeaves) {
            try {
                int csvEmpId = Integer.parseInt(row[1].trim());
                if (csvEmpId == targetEmpId) {
                    filtered.add(row);
                }
            } catch (Exception e) {
            }
        }
        return filtered.toArray(new Object[0][]);
    }

    public void updateStatus(String requestId, String newStatus) {
        leaveLibrary.updateLeaveStatus(requestId, newStatus);
    }

    public Object[][] getAllPendingLeaves() {
        List<String[]> all = leaveLibrary.fetchAllLeaves();
        List<Object[]> pending = new ArrayList<>();
        for (String[] row : all) {
            if (row.length > 8 && row[8].equalsIgnoreCase("PENDING")) {
                pending.add(row);
            }
        }
        return pending.toArray(new Object[0][]);
    }

    public Employee getEmployeeSalaryInfo(int empId) {
        return employeeDao.findById(empId);
    }


    public boolean updateEmployeeProfile(Employee emp) {
    if (emp == null) return false;
    
    return employeeDao.update(emp);
}

public int getTotalRemainingBalance(int empNo) {
    return getRemainingBalance(empNo, "Vacation Leave") + 
           getRemainingBalance(empNo, "Sick Leave");
}

public int getRemainingBalance(int empNo, String leaveType) {
    int totalAllowed = switch (leaveType) {
        case "Vacation Leave" -> MAX_VACATION;
        case "Sick Leave" -> MAX_SICK;
        case "Emergency Leave" -> MAX_EMERGENCY;
        case "Maternity Leave" -> MAX_MATERNITY;
        case "Paternity Leave" -> MAX_PATERNITY;
        default -> 0;
    };

    List<String[]> allLeaves = leaveLibrary.fetchAllLeaves();
    double used = 0;

    // We define both formats to prevent the "index 2" parse error
    java.time.format.DateTimeFormatter dbDash = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
    java.time.format.DateTimeFormatter csvSlash = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    for (String[] row : allLeaves) {
        try {
            if (row.length >= 9 && 
                row[1].equals(String.valueOf(empNo)) && 
                row[4].equalsIgnoreCase(leaveType) && 
                (row[8].equalsIgnoreCase("APPROVED") || row[8].equalsIgnoreCase("PENDING"))) {
                
                String startStr = row[5].trim();
                String endStr = row[6].trim();
                
                LocalDate start;
                LocalDate end;

                // FIX: Check if the date comes from DB (-) or CSV (/)
                if (startStr.contains("-")) {
                    start = LocalDate.parse(startStr, dbDash);
                    end = LocalDate.parse(endStr, dbDash);
                } else {
                    start = LocalDate.parse(startStr, csvSlash);
                    end = LocalDate.parse(endStr, csvSlash);
                }

                used += (java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
            }
        } catch (Exception e) {
            // This prevents one bad row from crashing your entire Dashboard
            System.err.println("Skipping invalid leave row: " + e.getMessage());
        }
    }
    return (int) (totalAllowed - used);
}




public Object[][] getAllLeaveRequests() {
    List<String[]> rawData = leaveLibrary.fetchAllLeaves();
    Object[][] tableData = new Object[rawData.size()][9];
    
    for (int i = 0; i < rawData.size(); i++) {
        tableData[i] = rawData.get(i);
    }
    return tableData;
}


public void updateLeaveStatus(String requestId, String status) {
    leaveLibrary.updateLeaveStatus(requestId, status);
}



}