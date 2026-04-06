package service;

import java.util.List;

import dao.EmployeeDAO;
import dao.LeaveLibrary;
import model.Employee;
import model.HRStaff;
import model.LeaveRequest;


public class HRSerbisyo {
    private final EmployeeDAO employeeDao;
    private final LeaveLibrary leaveLibrary; 

    public HRSerbisyo(EmployeeDAO employeeDao) {
        this.employeeDao = employeeDao;
        this.leaveLibrary = new LeaveLibrary(); 
    }


   
public boolean canAccessApprovalPanels(Employee user) {
    if (user == null) return false;
    String pos = user.getPosition();
    return pos.equalsIgnoreCase("HR Manager") || 
           pos.equalsIgnoreCase("Admin") || 
           pos.equalsIgnoreCase("Executive Management");
}

    
public boolean updateLeaveStatus(Employee actor, String requestId, String status) {
        if (requestId == null || requestId.trim().isEmpty()) return false;
        
       
        if (!isAuthorized(actor)) {
            throw new SecurityException("Access Denied: " + actor.getFirstName() + 
                " (" + actor.getRole().name() + ") is not authorized to modify leave requests.");
        }

        try {
            
            return leaveLibrary.updateLeaveStatus(requestId, status);
        } catch (Exception e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    
    private boolean isAuthorized(Employee actor) {
        if (actor == null || actor.getRole() == null) return false;
        
       
        String roleName = actor.getRole().name();
        
        return roleName.equalsIgnoreCase("HR_STAFF") || 
               roleName.equalsIgnoreCase("ADMIN");
    }

    public void approveLeave(HRStaff actor, String leaveID) {
        
        updateLeaveStatus(actor, leaveID, "Approved"); 
    }

    public void rejectLeave(HRStaff actor, String leaveID) {
        updateLeaveStatus(actor, leaveID, "Rejected"); 
    }


  
    public Object[][] getAllLeaveRequestsForTable() {
        List<String[]> rawData = leaveLibrary.fetchAllLeaves();
        Object[][] tableData = new Object[rawData.size()][9];
        
        for (int i = 0; i < rawData.size(); i++) {
            tableData[i] = rawData.get(i);
        }
        return tableData;
    }

    
    public Object[][] getLeaveHistory(int empNo) {
        List<String[]> allLeaves = leaveLibrary.fetchAllLeaves();
        return allLeaves.stream()
            .filter(row -> row.length >= 2 && row[1].equals(String.valueOf(empNo)))
            .toArray(Object[][]::new);
    }

    
    public int getPendingLeaveCount() {
        List<String[]> allLeaves = leaveLibrary.fetchAllLeaves();
        int count = 0;
        for (String[] row : allLeaves) {
            if (row != null && row.length >= 9 && "Pending".equalsIgnoreCase(row[8])) {
                count++;
            }
        }
        return count;
    }

public boolean registerNewEmployee(Employee e, byte[] photo) {
        // Validation logic preserved
        if (e.getEmpNo() <= 0 || e.getLastName().isEmpty()) return false; 
        
        // FIXED: Passing the photo bytes to the DAO
        return employeeDao.addEmployee(e, photo); 
    }
    public boolean removeEmployee(int id) {
        if (id == 10001) { 
            System.out.println("Error: System Admin cannot be deleted.");
            return false;
        }
        return employeeDao.deleteEmployee(id);
    }

    public Employee getEmployeeById(int id) {
        return employeeDao.findById(id); 
    }

    public List<LeaveRequest> getAllLeaveRequestsList() {
        return employeeDao.getAllLeaveRequestsList();
    }

    
}