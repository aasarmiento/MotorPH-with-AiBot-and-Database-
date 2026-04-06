package dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;

import model.Employee;
import model.LeaveRequest;


public interface EmployeeDAO {

   
   
    Employee findById(int empNo);
    Employee findByUsername(String username);
    List<Employee> getAll();
    public boolean addEmployee(Employee emp, byte[] photoBytes);
    boolean update(Employee emp);
    boolean deleteEmployee(int id);
    public boolean create(Employee emp);

    ImageIcon getDashboardIcon(String iconName);    
    
    void applyForLeave(int id, String type, String start, String end, String reason);
    Object[][] getLeaveStatusByEmpId(int empId);
    Object[][] getAllLeaveRequests();
    void updateLeaveStatus(String reqId, String stat);
    List<LeaveRequest> getAllLeaveRequestsList();

    void updateEmployeeStatus(int id, String stat);
    void saveNewPassword(int id, String pass);

    List<Employee> findAll();

    
    default int getNextAvailableId() {
        List<Employee> all = getAll();
        if (all == null || all.isEmpty()) return 10001;
        return all.stream()
                  .mapToInt(Employee::getEmpNo)
                  .max()
                  .getAsInt() + 1;
    }
public Employee getById(int id);
    
int getLastEmployeeNumber();
 
public void unlockAccount(int employeeNo);
List<Employee> getAllEmployees();

    void saveProfilePicture(int empNo, File sourceFile) throws IOException;

     public File getEmployeePhotoFile(int empId);

     public boolean createLoginCredentials(int empId, String username, String password, String role);

     public ImageIcon getEmployeePhoto(int id);
     
}   